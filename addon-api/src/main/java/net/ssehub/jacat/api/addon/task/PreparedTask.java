package net.ssehub.jacat.api.addon.task;

import net.ssehub.jacat.api.addon.data.SubmissionCollection;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PreparedTask {
    private final String id;

    private final String analyisSlug;

    private final String codeLanguage;

    private final Path workspace;

    private final SubmissionCollection submissions;

    private final Map<String, Object> request;

    public PreparedTask(Task task, Path workspace, SubmissionCollection submissions) {
        this.id = task.getId();
        this.analyisSlug = task.getDataProcessingRequest().getAnalysisSlug();
        this.codeLanguage = task.getDataProcessingRequest().getCodeLanguage();
        this.request = task.getRequest();
        this.workspace = workspace;
        this.submissions = submissions;
    }

    public Map<String, Object> getRequest() {
        return request;
    }

    public FinishedTask success(Map<String, Object> result) {
        return new FinishedTask(this, Task.Status.SUCCESSFUL, result);
    }

    public FinishedTask fail(Exception ex) {
        Map<String, Object> errorMap = new HashMap<>();
        errorMap.put("message", ex.getMessage());
        errorMap.put("code", ex.getClass().getSimpleName());
        if (ex.getCause() != null && ex.getCause().getMessage() != null) {
            errorMap.put("cause", ex.getCause().getMessage());
        }

        return fail(errorMap);
    }

    public FinishedTask fail(Map<String, Object> result) {
        Map<String, Object> errorResult = new HashMap<>();
        errorResult.put("error", result);
        return new FinishedTask(this, Task.Status.FAILED, errorResult);
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
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

}
