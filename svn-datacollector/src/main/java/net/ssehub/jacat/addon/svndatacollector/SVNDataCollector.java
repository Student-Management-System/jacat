package net.ssehub.jacat.addon.svndatacollector;

import com.sun.jna.platform.FileUtils;
import net.ssehub.jacat.api.addon.data.AbstractDataCollector;
import net.ssehub.jacat.api.addon.data.DataRequest;
import net.ssehub.jacat.api.addon.data.Submission;
import net.ssehub.jacat.api.addon.data.SubmissionCollection;
import org.slf4j.Logger;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNUpdateClient;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Stream;

public class SVNDataCollector extends AbstractDataCollector {

    private SVNUpdateClient updateClient;
    private SVNURL svnUrl;
    private Path workdir;

    private Logger logger;

    public SVNDataCollector(Logger logger, SVNUpdateClient updateClient, Path workdir, SVNURL url) {
        super("svn-java");
        this.logger = logger;
        this.svnUrl = url;
        this.workdir = workdir;
        this.updateClient = updateClient;
    }

    private Path arrange(DataRequest request) {
        Path directory = this.workdir.resolve(Path.of("request_" + request.hashCode()));
        try {
            updateClient.doCheckout(this.svnUrl,
                    directory.toFile(),
                    SVNRevision.HEAD,
                    SVNRevision.HEAD,
                    SVNDepth.INFINITY,
                    true);
            return directory;
        } catch (SVNException e) {
            this.logger.error("Couldn't checkout SVN URL " + this.svnUrl, e);
            throw new RuntimeException("Couldn't checkout SVN URL " + this.svnUrl, e);
        }
    }

    @Override
    public SubmissionCollection collect(DataRequest dataRequest) {
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
    public void clear(DataRequest request) {
        Path directory = this.workdir.resolve(Path.of("request_" + request.hashCode()));
        try {
            Files.walk(directory)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        } catch (IOException e) {

        }
    }

}
