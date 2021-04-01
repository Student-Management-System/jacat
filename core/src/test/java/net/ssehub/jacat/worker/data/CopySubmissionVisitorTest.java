package net.ssehub.jacat.worker.data;

import net.ssehub.jacat.api.addon.data.Submission;
import net.ssehub.jacat.api.addon.data.SubmissionVisitor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class CopySubmissionVisitorTest {

    public static final String A_COURSE = "A_COURSE";
    public static final String A_HOMEWORK = "A_HOMEWORK";
    public static final String A_SUBMISSION = "A_SUBMISSION";
    public static final String A_BASE_PATH = "A_BASE_PATH";
    Path temp;
    Path source;
    Path target;

    @BeforeEach
    void setUp() throws IOException, URISyntaxException {
        temp = Files.createTempDirectory("copySubmissionVisitorTest").toAbsolutePath();
        source = temp.resolve("source");
        source.toFile().mkdirs();
        target = temp.resolve("target");
        target.toFile().mkdirs();
    }

    @Test
    void visit_validSubmission_shouldCopyToTarget() {
        SubmissionVisitor copier = new CopySubmissionVisitor(this.target);
        Path basePath = source.resolve(A_BASE_PATH);
        basePath.toFile().mkdirs();
        Submission submission = new Submission(A_COURSE, A_HOMEWORK, A_SUBMISSION, basePath);
        submission.accept(copier);

        assertTrue(submission.getBasePath().startsWith(target));
    }

    @Test
    void visit_inValidSubmission_shouldNotCopyToTarget() {
        SubmissionVisitor copier = new CopySubmissionVisitor(this.target);
        Path basePath = source.resolve(A_BASE_PATH);
        // Dont mkdir basePath, so we can trigger an IOException

        Submission submission = new Submission(A_COURSE, A_HOMEWORK, A_SUBMISSION, basePath);

        assertThrows(RuntimeException.class, () -> {
            submission.accept(copier);
        });


    }
}