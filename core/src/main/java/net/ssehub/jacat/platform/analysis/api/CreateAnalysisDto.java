package net.ssehub.jacat.platform.analysis.api;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.ssehub.jacat.api.addon.data.DataProcessingRequest;
import net.ssehub.jacat.api.addon.task.TaskMode;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateAnalysisDto {

    private Data data;

    @JsonIgnore
    private TaskMode mode;

    private Map<String, Object> request = new HashMap<>();

    @JsonAnySetter
    public void setRequestParameter(String key, Object value) {
        this.request.put(key, value);
    }

    @lombok.Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Data {

        private String course;
        private String homework;
        private String submission;

    }
}
