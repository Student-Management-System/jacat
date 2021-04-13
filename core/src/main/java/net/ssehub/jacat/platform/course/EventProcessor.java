package net.ssehub.jacat.platform.course;

import net.ssehub.jacat.api.addon.data.DataProcessingRequest;
import net.ssehub.jacat.platform.analysis.AnalysisService;
import net.ssehub.jacat.platform.analysis.api.CreateAnalysisDto;
import net.ssehub.jacat.platform.course.config.CourseConfig;
import net.ssehub.jacat.platform.course.config.EventListenerConfig;
import org.springframework.stereotype.Component;

@Component
public class EventProcessor {

    private final AnalysisService analysisService;

    public EventProcessor(AnalysisService analysisService) {
        this.analysisService = analysisService;
    }

    public void process(CourseConfig course, EventListenerConfig event) {
        CreateAnalysisDto createAnalysis = new CreateAnalysisDto();
        createAnalysis.setData(new DataProcessingRequest(
            course.getProtocol(),
            event.getAnalysis(),
            course.getLanguage(),
            course.getCourse(),
            null,
            null));
        createAnalysis.setRequest(event.getDefaultParams());

        this.analysisService.tryProcess(createAnalysis);
    }

}
