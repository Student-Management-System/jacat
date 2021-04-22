package net.ssehub.jacat.platform.analysis;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.ssehub.jacat.api.addon.data.DataProcessingRequest;
import net.ssehub.jacat.api.addon.task.Task;
import net.ssehub.jacat.api.addon.task.TaskMode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Document
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"language", "dataConfiguration"})
public class AnalysisTask extends Task {

    public AnalysisTask(Task task) {
        super(
            task.getId(),
            task.getStatus(),
            task.getDataProcessingRequest().clone(),
            task.getRequest(),
            task.getResult(),
            task.getMode()
        );
    }

    public AnalysisTask(DataProcessingRequest data, Map<String, Object> request, TaskMode mode) {
        super(null, null, data, request, mode);
    }

    @Override
    @Id
    public String getId() {
        return super.getId();
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
