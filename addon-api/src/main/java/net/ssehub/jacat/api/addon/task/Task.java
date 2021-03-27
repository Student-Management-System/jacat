package net.ssehub.jacat.api.addon.task;

import net.ssehub.jacat.api.addon.data.DataProcessingRequest;

import java.util.Map;
import java.util.Objects;

public class Task {
    private String id;

    private Status status;

    private DataProcessingRequest dataProcessingRequest;

    private Map<String, Object> request;

    private Map<String, Object> result;

    public Task() {
    }

    public Task(
        String id,
        Status status,
        DataProcessingRequest collectRequest,
        Map<String, Object> request
    ) {
        this.id = id;
        this.status = status;
        this.dataProcessingRequest = collectRequest;
        this.request = request;
    }

    public Task(
        String id,
        Status status,
        DataProcessingRequest collectRequest,
        Map<String, Object> request,
        Map<String, Object> result
    ) {
        this.id = id;
        this.status = status;
        this.dataProcessingRequest = collectRequest;
        this.request = request;
        this.result = result;
    }
    
    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setResult(Status status, Map<String, Object> result) {
        this.status = status;
        this.result = result;
    }

    public Map<String, Object> getRequest() {
        return request;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public DataProcessingRequest getDataProcessingRequest() {
        return this.dataProcessingRequest;
    }

    public Map<String, Object> getResult() {
        return result;
    }

    @Override
    public String toString() {
        return "Task{" +
            "id='" + id + '\'' +
            ", status=" + status +
            ", dataProcessingRequest=" + dataProcessingRequest +
            ", request=" + request +
            ", result=" + result +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Task task = (Task) o;
        return Objects.equals(id, task.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public enum Status {
        SUCCESSFUL(),
        FAILED()
    }
}
