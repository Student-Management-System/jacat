package net.ssehub.jacat.api.addon.data;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataRequest {

    private Pattern course;
    private Pattern homework;
    private Pattern submission;

    public DataRequest(String course, String homework, String submission) {
        this.course = course != null ? Pattern.compile(course) : Pattern.compile(".*");
        this.homework = homework != null ? Pattern.compile(homework) : Pattern.compile(".*");
        this.submission = submission != null ? Pattern.compile(submission) : Pattern.compile(".*");
    }

    public Pattern getCourse() {
        return course;
    }

    public boolean courseMatches(String str) {
        return this.course.matcher(str).matches();
    }

    public Pattern getHomework() {
        return homework;
    }

    public boolean homeworkMatches(String str) {
        return this.homework.matcher(str).matches();
    }

    public Pattern getSubmission() {
        return submission;
    }

    public boolean submissionMatches(String str) {
        return this.submission.matcher(str).matches();
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
