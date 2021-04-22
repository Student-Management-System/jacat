package net.ssehub.jacat.platform.analysis;

import net.ssehub.jacat.api.addon.task.Task;
import net.ssehub.jacat.api.analysis.IAnalysisCapabilities;
import net.ssehub.jacat.api.analysis.IAnalysisTaskExecutor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@EnableScheduling
public class AnalysisMonitor {
    private IAnalysisCapabilities capabilities;
    private AnalysisTaskRepository repository;
    private AnalysisService analysisService;
    private IAnalysisTaskExecutor taskExecutor;

    public AnalysisMonitor(
        IAnalysisCapabilities capabilities,
        AnalysisTaskRepository repository,
        AnalysisService analysisService,
        IAnalysisTaskExecutor taskExecutor) {
        this.capabilities = capabilities;
        this.repository = repository;
        this.analysisService = analysisService;
        this.taskExecutor = taskExecutor;
    }

    @Scheduled(fixedRate = 30000, initialDelay = 0)
    private void checkAndRerun() {
        List<AnalysisTask> unprocessed = this.repository.findAllByStatus(null);
        unprocessed.stream()
            .map(analysisTask -> new Task(
                analysisTask.getId(),
                analysisTask.getStatus(),
                analysisTask.getDataProcessingRequest(),
                analysisTask.getRequest(),
                analysisTask.getResult(),
                analysisTask.getMode())
            )
            .filter(taskExecutor::isRunning)
            .filter(task -> capabilities.isRegistered(
                task.getDataProcessingRequest().getAnalysisSlug(),
                task.getDataProcessingRequest().getCodeLanguage()
            ))
            .forEach(analysisService::process);
    }
}
