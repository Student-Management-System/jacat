package net.ssehub.jacat.platform.course;

import lombok.extern.slf4j.Slf4j;
import net.ssehub.jacat.api.addon.data.DataProcessingRequest;
import net.ssehub.jacat.api.addon.task.TaskMode;
import net.ssehub.jacat.platform.analysis.AnalysisService;
import net.ssehub.jacat.platform.analysis.api.CreateAnalysisDto;
import net.ssehub.jacat.platform.auth.StudMgmtClient;
import net.ssehub.jacat.platform.course.config.CourseConfig;
import net.ssehub.jacat.platform.course.config.EventListenerConfig;
import net.ssehub.studentmgmt.backend_api.ApiException;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EventProcessor {

    private final AnalysisService analysisService;
    private final StudMgmtClient studMgmtClient;

    public EventProcessor(AnalysisService analysisService,
                            StudMgmtClient studMgmtClient) {
        this.analysisService = analysisService;
        this.studMgmtClient = studMgmtClient;
    }

    public void process(String assignmentId,
                        CourseConfig course,
                        EventListenerConfig event) {
        try {
            String homework = this.studMgmtClient.getAssignmentsApi()
                .getAssignmentById(course.getCourse(), assignmentId)
                .getName();

            CreateAnalysisDto createAnalysis = new CreateAnalysisDto();
            createAnalysis.setMode(TaskMode.ASYNC);
            createAnalysis.setRequest(event.getDefaultParams());
            DataProcessingRequest dpr = new DataProcessingRequest(
                course.getProtocol(),
                event.getAnalysis(),
                course.getLanguage(),
                course.getCourse(),
                homework,
                null);

            this.analysisService.tryProcess(createAnalysis, dpr);
        } catch (ApiException e) {
            log.error("Cannot get Assignment: " + assignmentId, e);
        }

    }

}
