package net.ssehub.jacat.platform.analysis.api;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.ssehub.jacat.api.addon.data.DataSection;
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

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties({"protocol"})
    private DataSection data;
    private Map<String, Object> request = new HashMap<>();
    private Map<String, Object> result = new HashMap<>();

    public ListAnalysisResultDto(AnalysisTask analysisTask) {
        this.id = analysisTask.getId();
        this.slug = analysisTask.getSlug();
        this.data = analysisTask.getDataConfiguration().clone();
        this.data.setProtocol(null);
        this.request = analysisTask.getRequest();
        this.result = analysisTask.getResult();
    }
}
