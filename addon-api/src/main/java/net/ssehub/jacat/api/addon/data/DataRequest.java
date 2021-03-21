package net.ssehub.jacat.api.addon.data;

import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Each data request consists of a homework
 * and a submission to be indexed by the DataCollector.
 */
public class DataRequest {
    private final Pattern homework;
    private final Pattern submission;
    private final UUID uuid;

    /**
     * When a data request is created, a UUID is
     * generated so that a request can be identified
     * again over a processing period.
     *
     * @param homework   The homework which was requested
     * @param submission The submission which was requested
     */
    public DataRequest(String homework, String submission) {
        this.homework =
            homework != null ? Pattern.compile(homework) : Pattern.compile(".*");
        this.submission =
            submission != null ? Pattern.compile(submission) : Pattern.compile(".*");
        this.uuid = UUID.randomUUID();
    }

    /**
     * Getter for the UUID.
     *
     * @return the assigned UUID
     */
    public UUID getUuid() {
        return uuid;
    }

    /**
     * Getter for the homework.
     *
     * @return the homework, which should be processed
     */
    public Pattern getHomework() {
        return homework;
    }

    /**
     * Checks if a given String is matching the desired homework.
     *
     * @param str the string which should be checked
     * @return {@code true} if the given string matches
     * the desired homework
     */
    public boolean homeworkMatches(String str) {
        return this.homework.matcher(str).matches();
    }

    /**
     * Getter for the submission.
     *
     * @return the submission, which should be processed.
     */
    public Pattern getSubmission() {
        return submission;
    }

    /**
     * Checks if a given String is matching the desired submission.
     *
     * @param str the string which should be checked
     * @return {@code true} if the given string matches
     * the desired submission
     */
    public boolean submissionMatches(String str) {
        return this.submission.matcher(str).matches();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        DataRequest that = (DataRequest) obj;
        return (homework.equals(that.homework)
            && submission.equals(that.submission)
            && uuid.equals(that.uuid));
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }
}
