package net.ssehub.jacat.api.addon.task;

import java.util.Map;

public class FinishedTask {

    private final String id;

    private final Map<String, Object> request;
    private final Map<String, Object> result;

    private final Task.Status status;

    public FinishedTask(PreparedTask preparedTask, Task.Status status, Map<String, Object> result) {
        this.id = preparedTask.getId();
        this.request = preparedTask.getRequest();
        this.result = result;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public Map<String, Object> getRequest() {
        return request;
    }

    public Map<String, Object> getResult() {
        return result;
    }

    public Task.Status getStatus() {
        return status;
    }
    
}
