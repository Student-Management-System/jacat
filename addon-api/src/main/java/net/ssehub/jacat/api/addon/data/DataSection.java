package net.ssehub.jacat.api.addon.data;

/**
 * DataSection describes the part which got passed in
 * by the api-user. It contains the protocol, the
 * desired course, the desired homework and the desired
 * submission.
 */
public class DataSection {
    private String protocol;

    private String course;
    private String homework;
    private String submission;

    /**
     * Default Constructor. Used by deserialization.
     */
    public DataSection() {
    }

    /**
     * All Args-Constructor for DataSection.
     *
     * @param protocol   The protocol which should be used.
     * @param course     The desired course.
     * @param homework   The desired homework.
     * @param submission The desired submission.
     */
    public DataSection(
        String protocol,
        String course,
        String homework,
        String submission
    ) {
        this.protocol = protocol;
        this.course = course;
        this.homework = homework;
        this.submission = submission;
    }

    /**
     * Getter for course.
     *
     * @return the course
     */
    public String getCourse() {
        return course;
    }

    /**
     * Setter for course.
     *
     * @param course the course
     */
    public void setCourse(String course) {
        this.course = course;
    }

    /**
     * Getter for homework.
     *
     * @return the homework
     */
    public String getHomework() {
        return homework;
    }

    /**
     * Setter for homework.
     *
     * @param homework the homework
     */
    public void setHomework(String homework) {
        this.homework = homework;
    }

    /**
     * Getter for submission.
     *
     * @return the submission
     */
    public String getSubmission() {
        return submission;
    }

    /**
     * Setter for submission.
     *
     * @param submission the submission
     */
    public void setSubmission(String submission) {
        this.submission = submission;
    }

    /**
     * Getter for protocol.
     *
     * @return the protocol
     */
    public String getProtocol() {
        return protocol;
    }

    /**
     * Setter for protocol.
     *
     * @param protocol the protocol
     */
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    /**
     * Clones the given object.
     *
     * @return a clone of this object.
     */
    public DataSection clone() {
        return new DataSection(
            this.getProtocol(),
            this.getCourse(),
            this.getHomework(),
            this.getSubmission());
    }

    @Override
    public String toString() {
        return ("DataSection{"
            + "protocol='" + protocol + '\''
            + ", course='" + course + '\''
            + ", homework='" + homework + '\''
            + ", submission='" + submission + '\''
            + '}');
    }
}
