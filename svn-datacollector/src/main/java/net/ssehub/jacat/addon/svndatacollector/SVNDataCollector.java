package net.ssehub.jacat.addon.svndatacollector;

import net.ssehub.jacat.api.addon.data.AbstractDataCollector;
import net.ssehub.jacat.api.addon.data.DataRequest;
import net.ssehub.jacat.api.addon.data.Submission;
import net.ssehub.jacat.api.addon.data.SubmissionCollection;
import org.slf4j.Logger;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.io.ISVNEditor;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.Stream;

public class SVNDataCollector extends AbstractDataCollector {

    private final SVNRepository repository;
    private Logger logger;
    private Path source;

    public SVNDataCollector(Logger logger) {
        super("svn-java");
        this.logger = logger;

        String URL = "svn://localhost/java";
        try {
            SVNURL svnUrl = SVNURL.parseURIEncoded(URL);
            repository = SVNRepositoryFactory.create(svnUrl);

            ISVNAuthenticationManager authManager = SVNWCUtil
                    .createDefaultAuthenticationManager("test", "secret");
            repository.setAuthenticationManager(authManager);
        } catch (SVNException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void arrange(DataRequest request) {
        try {
            long latestRevision = repository.getLatestRevision();
            Path directory = Files.createTempDirectory("jacat_svn_dc");
            this.logger.info("Checkout revision " + latestRevision + ": " + directory.toString());
            ISVNEditor exportEditor = new SimpleCheckoutEditor(directory.toFile());
            repository.checkout(latestRevision, null, true, exportEditor);
            this.source = directory;
        } catch (SVNException | IOException e) {
            throw new RuntimeException();
        }
    }

    @Override
    public SubmissionCollection collect(DataRequest dataRequest) {
        SubmissionCollection submissions = new SubmissionCollection();
        if (dataRequest.getCourse() != null
                && dataRequest.getHomework() == null
                && dataRequest.getSubmission() == null) { // get all submissions for all homeworks
            File baseCopyFrom = new File(this.source.toString());
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
