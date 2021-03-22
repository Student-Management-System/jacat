package net.ssehub.jacat.worker.data;

import net.ssehub.jacat.api.addon.data.FolderUtils;
import net.ssehub.jacat.api.addon.data.Submission;
import net.ssehub.jacat.api.addon.data.SubmissionVisitor;

import java.io.IOException;
import java.nio.file.Path;
import java.util.UUID;

public class CopySubmissionVisitor implements SubmissionVisitor {
    private Path target;

    public CopySubmissionVisitor(Path target) {
        this.target = target;
    }

    @Override
    public void visit(Submission submission) throws RuntimeException {
        String salt = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 6);
        String folderName =
            "sub_" +
                submission.getSubmission().replaceAll("[^a-zA-Z0-9]*", "") +
                "_" +
                salt;

        Path oldBasePath = submission.getBasePath().toAbsolutePath();
        Path newBasePath = this.target.resolve(folderName);
        try {
            FolderUtils.copyFolder(oldBasePath, target, folderName);
            submission.setBasePath(newBasePath);
        } catch (IOException e) {
            throw new CannotCopySubmissionException(submission, e);
        }
    }

    private class CannotCopySubmissionException extends RuntimeException {

        public CannotCopySubmissionException(Submission submission, Throwable cause) {
            super("Cannot move Submission: ", cause);
        }
    }
}
