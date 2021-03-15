package net.ssehub.jacat.api.addon.data;

import java.util.Objects;
import java.util.regex.Pattern;

public class DataRequest {

    private Pattern homework;
    private Pattern submission;

    public DataRequest(String homework, String submission) {
        this.homework = homework != null ? Pattern.compile(homework) : Pattern.compile(".*");
        this.submission = submission != null ? Pattern.compile(submission) : Pattern.compile(".*");
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
        return Objects.equals(homework.pattern(), that.homework.pattern()) &&
                Objects.equals(submission.pattern(), that.submission.pattern());
    }

    @Override
    public int hashCode() {
        return Objects.hash(homework.pattern(), submission.pattern());
    }
}
