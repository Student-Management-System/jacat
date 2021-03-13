package net.ssehub.jacat.api.addon.data;

import java.util.Objects;

public class DataRequest {

    private String course;
    private String homework;
    private String submission;

    public DataRequest(String course, String homework, String submission) {
        this.course = course;
        this.homework = homework;
        this.submission = submission;
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
        DataRequest that = (DataRequest) o;
        return Objects.equals(course, that.course) &&
                Objects.equals(homework, that.homework) &&
                Objects.equals(submission, that.submission);
    }

    @Override
    public int hashCode() {
        return Objects.hash(course, homework, submission);
    }
}
