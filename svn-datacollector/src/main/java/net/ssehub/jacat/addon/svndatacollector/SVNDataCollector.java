package net.ssehub.jacat.addon.svndatacollector;

import net.ssehub.jacat.api.addon.data.AbstractDataCollector;
import net.ssehub.jacat.api.addon.data.DataRequest;
import net.ssehub.jacat.api.addon.data.Submission;
import net.ssehub.jacat.api.addon.data.SubmissionCollection;
import org.slf4j.Logger;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.BasicAuthenticationManager;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.io.ISVNEditor;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNUpdateClient;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import javax.xml.crypto.Data;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Stream;

public class SVNDataCollector extends AbstractDataCollector {

    private SVNUpdateClient updateClient;
    private SVNURL svnUrl;
    private Path workdir;

    private Logger logger;

    public SVNDataCollector(Logger logger, SVNUpdateClient updateClient, Path workdir, String url) {
        super("svn-java");
        this.logger = logger;

        try {
            this.svnUrl = SVNURL.parseURIEncoded(url);
        } catch (SVNException e) {
            this.logger.error("Couldn't parse URL: " + url, e);
            throw new RuntimeException("Couldn't parse URL: " + url, e);
        }
        this.workdir = workdir;
        this.updateClient = updateClient;
    }

    private Path arrange(DataRequest request) {
        Path directory = this.workdir.resolve(Path.of("request_" + request.hashCode()));
        try {
            updateClient.doCheckout(this.svnUrl, directory.toFile(), SVNRevision.HEAD, SVNRevision.HEAD, SVNDepth.INFINITY, true);
            return directory;
        } catch (SVNException e) {
            this.logger.error("Couldn't checkout SVN URL " + this.svnUrl, e);
            throw new RuntimeException("Couldn't checkout SVN URL " + this.svnUrl, e);
        }
    }

    @Override
    public SubmissionCollection collect(DataRequest dataRequest) {
        SubmissionCollection submissions = new SubmissionCollection();
        if (dataRequest.getCourse() != null
                && dataRequest.getHomework() == null
                && dataRequest.getSubmission() == null) {
            Path source = arrange(dataRequest);
            File baseCopyFrom = new File(source.toString());
            Stream<File> stream = Arrays.stream(baseCopyFrom.listFiles());

            stream.filter(File::isDirectory).forEach(homework -> {
                File[] submissionFiles = homework.listFiles();
                for(File submissionFolder : submissionFiles) {
                    if (submissionFolder.isFile()) {
                        continue;
                    }
                    Submission java = new Submission("java",
                            homework.getName(),
                            submissionFolder.getName(),
                            submissionFolder.toPath());
                    submissions.add(java);
                }
            });
        }

        return submissions;
    }

}
