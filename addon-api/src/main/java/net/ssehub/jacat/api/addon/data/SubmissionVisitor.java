package net.ssehub.jacat.api.addon.data;

public interface SubmissionVisitor {
    void visit(Submission submission) throws RuntimeException;
}
