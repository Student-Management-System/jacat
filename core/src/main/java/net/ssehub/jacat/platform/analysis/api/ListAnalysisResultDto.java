package net.ssehub.jacat.platform.analysis.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.ssehub.jacat.api.addon.data.DataProcessingRequest;
import net.ssehub.jacat.api.addon.task.TaskMode;
import net.ssehub.jacat.platform.analysis.AnalysisTask;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ListAnalysisResultDto {
    private String id;

    private String slug;
    private String course;
    private String homework;
    private String submission;

    private TaskMode mode;

    private Map<String, Object> request = new HashMap<>();
    private Map<String, Object> result = new HashMap<>();

    public ListAnalysisResultDto(AnalysisTask analysisTask) {
        this.id = analysisTask.getId();
        DataProcessingRequest data = analysisTask.getDataProcessingRequest().clone();
        this.slug = data.getAnalysisSlug();
        this.course = data.getCourse();
        this.homework = data.getHomework();
        this.submission = data.getSubmission();
        this.request = analysisTask.getRequest();
        this.result = analysisTask.getResult();
        this.mode = analysisTask.getMode();
    }
}
