package net.ssehub.jacat.addon.svndatacollector;

import net.ssehub.jacat.api.addon.data.AbstractDataCollector;
import net.ssehub.jacat.api.addon.data.DataProcessingRequest;
import net.ssehub.jacat.api.addon.data.Submission;
import net.ssehub.jacat.api.addon.data.SubmissionCollection;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.BasicAuthenticationManager;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNInfo;
import org.tmatesoft.svn.core.wc.SVNRevision;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Stream;

public class SVNDataCollector extends AbstractDataCollector {

    private final String username;
    private final String password;
    private final SVNURL url;
    private Path workdir;

    private Logger logger;

    public SVNDataCollector(Logger logger, String username, String password, SVNURL url, Path workdir) {
        super("svn-java");
        this.logger = logger;
        this.username = username;
        this.password = password;
        this.url = url;
        this.workdir = workdir;
    }

    private SVNClientManager connect() {
        SVNClientManager clientManager = SVNClientManager.newInstance(null,
            BasicAuthenticationManager.newInstance(this.username,
                this.password.toCharArray()));

        try {
            SVNInfo svnInfo = clientManager.getWCClient()
                .doInfo(this.url, SVNRevision.HEAD, SVNRevision.HEAD);
//            this.getLogger().info("Connected to: " + url);
        } catch (SVNException e) {
            throw new RuntimeException("Couldn't connect to " + url + ", " + e.getCause(), e.getCause());
        }
        return clientManager;
    }

    private Path arrange(DataProcessingRequest request) {
        Path directory = this.workdir.resolve(Path.of("request_" + request.hashCode()));
        try {
            this.connect().getUpdateClient().doExport(this.url,
                directory.toFile(),
                SVNRevision.HEAD,
                SVNRevision.HEAD,
                null,
                true,
                SVNDepth.INFINITY);
            return directory;
        } catch (SVNException e) {
            this.logger.error("Couldn't checkout SVN URL " + this.url, e);
            throw new RuntimeException("Couldn't checkout SVN URL " + this.url, e);
        }
    }

    @Override
    public SubmissionCollection collect(DataProcessingRequest dataRequest) {
        Objects.requireNonNull(dataRequest);

        SubmissionCollection submissions = new SubmissionCollection();

        Path source = this.arrange(dataRequest);
        File base = source.toFile();

        Stream<File> homeworksStream = Arrays.stream(Objects.requireNonNull(base.listFiles()))
            .filter(File::isDirectory)
            .filter(file -> !file.getName().contains(".svn"))
            .filter(file -> dataRequest.homeworkMatches(file.getName()));


        homeworksStream.forEach(homework -> {
            Stream<File> submissionsStream = Arrays.stream(Objects.requireNonNull(homework.listFiles()))
                .filter(File::isDirectory)
                .filter(file -> !file.getName().contains(".svn"))
                .filter(file -> dataRequest.submissionMatches(file.toPath().getFileName().toString()));

            submissionsStream.map(submission -> new Submission("java",
                homework.getName(),
                submission.getName(),
                submission.toPath())
            ).forEach(submissions::add);
        });


        return submissions;
    }

    @Override
    public void cleanup(DataProcessingRequest request) {
        Path directory = this.workdir.resolve(Path.of("request_" + request.hashCode()));
        try {
            Files.walk(directory)
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);

            FileUtils.deleteDirectory(directory.toFile());
        } catch (IOException e) {
            this.logger.error("Cannot delete tmp folder: " + directory.toString(), e);
        }

        File workdir = directory.toFile();
        if (workdir.exists()) {
            workdir.delete();
        }

    }

}
