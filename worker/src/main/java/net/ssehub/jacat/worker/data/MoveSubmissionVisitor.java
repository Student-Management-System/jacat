package net.ssehub.jacat.worker.data;

import net.ssehub.jacat.api.addon.data.SubmissionVisitor;
import net.ssehub.jacat.api.addon.data.FolderUtils;
import net.ssehub.jacat.api.addon.data.Submission;

import java.io.IOException;
import java.nio.file.Path;
import java.util.UUID;

public class MoveSubmissionVisitor implements SubmissionVisitor {

    private Path target;

    public MoveSubmissionVisitor(Path target) {
        this.target = target;
    }

    @Override
    public void visit(Submission submission) throws RuntimeException {
        String salt = UUID.randomUUID().toString()
                .replaceAll("-", "")
                .substring(0, 6);
        String folderName = "sub_" + submission.getSubmission() + "_" + salt;

        Path oldBasePath = submission.getBasePath();
        Path newBasePath = this.target.resolve(folderName);

        try {
            FolderUtils.copyFolder(target, oldBasePath, folderName);
            submission.setBasePath(newBasePath);
        } catch (IOException e) {
            throw new CannotMoveSubmissionException(submission, e);
        }
    }

    private class CannotMoveSubmissionException extends RuntimeException {
        public CannotMoveSubmissionException(Submission submission, Throwable cause) {
            super("Cannot move Submission: ", cause);
        }
    }
}
