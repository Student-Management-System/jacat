package net.ssehub.jacat.api.addon.data;

/**
 * DataProcessingRequest describes what data should get
 * collected by a datacollector and also what data should
 * be processed by the. It contains information
 * given by the api user and also information which
 * is augmented by the platform itself. In general a
 * datacollector would change its collection strat
 */
public class DataProcessingRequest {

    // Data which helps DataCollectors
    private String dataCollector;
    private String course;
    private String homework;
    private String submission;

    // Data which helps AnalysisAddons
    private String analysisSlug;
    private String codeLanguage;

    /**
     * Default Constructor. Used by deserialization.
     */
    public DataProcessingRequest() {
    }

    /**
     * All-Args-Constructor for DataSection.
     *
     * @param dataCollector The protocol which should be used.
     * @param analysisSlug
     * @param codeLanguage
     * @param course        The desired course.
     * @param homework      The desired homework.
     * @param submission    The desired submission.
     */
    public DataProcessingRequest(String dataCollector,
                                 String analysisSlug,
                                 String codeLanguage,
                                 String course,
                                 String homework,
                                 String submission) {
        this.dataCollector = dataCollector;
        this.analysisSlug = analysisSlug;
        this.codeLanguage = codeLanguage;
        this.course = course;
        this.homework = homework;
        this.submission = submission;
    }

    /**
     * Getter for analysisSlug
     *
     * @return the Slug which identifies the Analysis
     */
    public String getAnalysisSlug() {
        return analysisSlug;
    }

    /**
     * Setter for analysisSlug
     *
     * @param analysisSlug the Slug which identifies the Analysis
     */
    public void setAnalysisSlug(String analysisSlug) {
        this.analysisSlug = analysisSlug;
    }

    /**
     * Getter for codeLanguage.
     *
     * @return the Code Language (such as java19, c++, c#, etc.)
     */
    public String getCodeLanguage() {
        return codeLanguage;
    }

    /**
     * Setter for codeLanguage.
     *
     * @param codeLanguage the Code Language
     */
    public void setCodeLanguage(String codeLanguage) {
        this.codeLanguage = codeLanguage;
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
    public String getDataCollector() {
        return dataCollector;
    }

    /**
     * Setter for protocol.
     *
     * @param dataCollector the protocol
     */
    public void setDataCollector(String dataCollector) {
        this.dataCollector = dataCollector;
    }

    /**
     * Checks if a given String is matching the desired course.
     *
     * @param check the string which should be checked
     * @return {@code true} if the given string matches
     * the desired course
     */
    public boolean courseMatches(String check) {
        if (this.course == null) {
            return true;
        }
        return this.course.equals(check);
    }

    /**
     * Checks if a given String is matching the desired homework.
     *
     * @param check the string which should be checked
     * @return {@code true} if the given string matches
     * the desired homework
     */
    public boolean homeworkMatches(String check) {
        if (this.homework == null) {
            return true;
        }
        return this.homework.equals(check);
    }

    /**
     * Checks if a given String is matching the desired submission.
     *
     * @param str the string which should be checked
     * @return {@code true} if the given string matches
     * the desired submission
     */
    public boolean submissionMatches(String str) {
        if (this.submission == null) {
            return true;
        }
        return this.submission.equals(str);
    }

    /**
     * Clones the given object.
     *
     * @return a clone of this object.
     */
    public DataProcessingRequest clone() {
        return new DataProcessingRequest(
            this.getDataCollector(),
            this.getAnalysisSlug(),
            this.getCodeLanguage(),
            this.getCourse(),
            this.getHomework(),
            this.getSubmission());
    }

    @Override
    public String toString() {
        return "DataProcessingRequest{" +
                "dataCollector='" + dataCollector + '\'' +
                ", course='" + course + '\'' +
                ", homework='" + homework + '\'' +
                ", submission='" + submission + '\'' +
                ", analysisSlug='" + analysisSlug + '\'' +
                ", codeLanguage='" + codeLanguage + '\'' +
                '}';
    }
}
