package net.ssehub.jacat.api.addon.data;

import java.util.*;

public class SubmissionCollection implements Iterable<Submission> {
    private final List<Submission> submissions;

    public SubmissionCollection() {
        this.submissions = new ArrayList<>();
    }

    public void add(Submission submission) {
        this.submissions.add(submission);
    }

    public void addAll(Collection<Submission> submissions) {
        this.submissions.addAll(submissions);
    }

    public Optional<Submission> getSubmission(String folderName) {
        return submissions
            .stream()
            .filter(
                submission ->
                    submission
                        .getBasePath()
                        .getFileName()
                        .toString()
                        .equalsIgnoreCase(folderName)
            )
            .findFirst();
    }

    @Override
    public Iterator<Submission> iterator() {
        return this.submissions.iterator();
    }

    public void accept(SubmissionVisitor visitor) {
        for (Submission submission : this.submissions) {
            try {
                submission.accept(visitor);
            } catch (RuntimeException e) {
                submissions.remove(submission);
                e.printStackTrace();
            }
        }
    }
}
