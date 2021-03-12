package net.ssehub.jacat.api.addon.data;

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
}
