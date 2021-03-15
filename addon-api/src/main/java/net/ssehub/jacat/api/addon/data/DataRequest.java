package net.ssehub.jacat.api.addon.data;

import java.util.Date;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

public class DataRequest {

    private Pattern homework;
    private Pattern submission;
    private UUID uuid;

    public DataRequest(String homework, String submission) {
        this.homework = homework != null ? Pattern.compile(homework) : Pattern.compile(".*");
        this.submission = submission != null ? Pattern.compile(submission) : Pattern.compile(".*");
        this.uuid = UUID.randomUUID();
    }

    public UUID getUuid() {
        return uuid;
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
        return homework.equals(that.homework) && submission.equals(that.submission) && uuid.equals(that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }
}
