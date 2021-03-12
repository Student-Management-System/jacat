package net.ssehub.jacat.api.addon.data;

import java.nio.file.Path;
import java.util.Objects;

public class Submission {

    private String course;
    private String homework;
    private String submission;
    private Path basePath;

    public Submission(String course, String homework, String submission, Path basePath) {
        this.course = course;
        this.homework = homework;
        this.submission = submission;
        this.basePath = basePath;
    }

    public Path getBasePath() {
        return basePath;
    }

    public void setBasePath(Path basePath) {
        this.basePath = basePath;
    }

    public String getCourse() {
        return course;
    }

    public String getHomework() {
        return homework;
    }

    public String getSubmission() {
        return submission;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Submission that = (Submission) o;
        return Objects.equals(course, that.course)
                && Objects.equals(homework, that.homework)
                && Objects.equals(submission, that.submission)
                && Objects.equals(basePath, that.basePath);
    }

    @Override
    public String toString() {
        return "Submission{" +
                "course='" + course + '\'' +
                ", homework='" + homework + '\'' +
                ", submission='" + submission + '\'' +
                ", basePath=" + basePath +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(course, homework, basePath, submission);
    }

    public void accept(SubmissionVisitor visitor){
        visitor.visit(this);
    }
}
