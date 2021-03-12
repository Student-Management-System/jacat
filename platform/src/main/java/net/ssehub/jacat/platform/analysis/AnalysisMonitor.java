package net.ssehub.jacat.platform.analysis;

import net.ssehub.jacat.api.analysis.IAnalysisCapabilities;
import net.ssehub.jacat.api.addon.task.Task;
import net.ssehub.jacat.api.analysis.IAnalysisScheduler;
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
    private IAnalysisTaskExecutor executor;
    private IAnalysisScheduler scheduler;
    private AnalysisService analysisService;

    public AnalysisMonitor(IAnalysisCapabilities capabilities,
                           AnalysisTaskRepository repository,
                           IAnalysisTaskExecutor executor,
                           IAnalysisScheduler scheduler,
                           AnalysisService analysisService) {
        this.capabilities = capabilities;
        this.repository = repository;
        this.executor = executor;
        this.scheduler = scheduler;
        this.analysisService = analysisService;
    }

    @Scheduled(fixedRate = 30000, initialDelay = 0)
    private void checkAndRerun() {
        List<AnalysisTask> unprocessed= this.repository.findAllByStatus(null);
        unprocessed.stream().map(analysisTask ->
                        new Task(analysisTask.getId(),
                        analysisTask.getSlug(),
                        analysisTask.getLanguage(),
                        analysisTask.getStatus(),
                        analysisTask.getDataConfiguration(),
                        analysisTask.getRequest(),
                        analysisTask.getResult()))
                .filter(task -> !scheduler.isScheduled(task) && !executor.isRunning(task))
                .filter(task -> capabilities.isRegistered(task.getSlug(), task.getLanguage()))
                .forEach(task -> {
                    System.out.println("AnalysisMonitor.checkAndRerun");
                    analysisService.schedule(task);
                });

    }

}
