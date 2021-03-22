package net.ssehub.jacat.api.addon.task;

import net.ssehub.jacat.api.addon.data.DataSection;

import java.util.Map;
import java.util.Objects;

public class Task {
    private String id;

    private String slug;

    private String language;

    private Status status;

    private DataSection dataConfiguration;

    private Map<String, Object> request;

    private Map<String, Object> result;

    public Task() {
    }

    public Task(
        String id,
        String slug,
        String language,
        DataSection dataSection,
        Map<String, Object> request
    ) {
        this.id = id;
        this.slug = slug;
        this.language = language;
        this.dataConfiguration = dataSection;
        this.request = request;
    }

    public Task(
        String id,
        String slug,
        String language,
        Status status,
        DataSection dataSection,
        Map<String, Object> request,
        Map<String, Object> result
    ) {
        this.id = id;
        this.slug = slug;
        this.language = language;
        this.status = status;
        this.dataConfiguration = dataSection;
        this.request = request;
        this.result = result;
    }

    public void setResult(Status status, Map<String, Object> result) {
        this.status = status;
        this.result = result;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public DataSection getDataConfiguration() {
        return this.dataConfiguration;
    }

    public Status getStatus() {
        return status;
    }

    public Map<String, Object> getResult() {
        return result;
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

    @Override
    public String toString() {
        return (
            "Task{id='" +
                id +
                '\'' +
                ", slug='" +
                slug +
                '\'' +
                ", language='" +
                language +
                '\'' +
                ", status=" +
                status +
                ", dataConfiguration=" +
                dataConfiguration +
                ", request=" +
                request +
                ", result=" +
                result +
                '}'
        );
    }

    public enum Status {
        SUCCESSFUL(),
        FAILED()
    }
}
