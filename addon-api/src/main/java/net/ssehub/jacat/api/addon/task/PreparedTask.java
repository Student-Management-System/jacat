package net.ssehub.jacat.api.addon.task;

import net.ssehub.jacat.api.addon.data.SubmissionCollection;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PreparedTask {

    private String id;

    private String slug;

    private String language;

    private Task.Status status;

    private Path workspace;

    private SubmissionCollection submissions;

    private Map<String, Object> request;

    private Map<String, Object> result;

    public PreparedTask(Task task) {
        this.id = task.getId();
        this.slug = task.getSlug();
        this.language = task.getLanguage();
        this.status = task.getStatus();
        this.request = task.getRequest();
    }

    public void setSubmissions(SubmissionCollection submissions) {
        this.submissions = submissions;
    }

    public Map<String, Object> getRequest() {
        return request;
    }

    public String getSlug() {
        return slug;
    }

    public String getLanguage() {
        return language;
    }

    public void setSuccessfulResult(Map<String, Object> result) {
        this.result = result;
        this.status = Task.Status.SUCCESSFUL;
    }

    public void setFailedResult(Map<String, Object> result) {
        this.result = new HashMap<>();
        this.result.put("error", result);
        this.status = Task.Status.FAILED;
    }

    public String getId() {
        return id;
    }

    public Task.Status getStatus() {
        return status;
    }

    public Map<String, Object> getResult() {
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PreparedTask task = (PreparedTask) o;
        return id.equals(task.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public SubmissionCollection getSubmissions() {
        return submissions;
    }

    public Path getWorkspace() {
        return workspace;
    }

    public void setWorkspace(Path workspace) {
        this.workspace = workspace;
    }
}
