package net.ssehub.jacat.platform.analysis;

import net.ssehub.jacat.api.addon.Addon;
import net.ssehub.jacat.api.analysis.IAnalysisCapabilities;
import net.ssehub.jacat.api.addon.task.Task;
import net.ssehub.jacat.platform.analysis.api.CreateAnalysisDto;
import net.ssehub.jacat.platform.course.CoursesConfiguration;
import net.ssehub.jacat.worker.analysis.queue.AnalysisTaskScheduler;
import org.springframework.stereotype.Service;

@Service
public class AnalysisService {

    private final IAnalysisCapabilities<Addon> capabilities;
    private final AnalysisTaskScheduler analysisTaskScheduler;
    private final AnalysisTaskRepository repository;

    public AnalysisService(IAnalysisCapabilities<Addon> capabilities,
                           AnalysisTaskScheduler analysisTaskScheduler,
                           AnalysisTaskRepository repository) {
        this.capabilities = capabilities;
        this.analysisTaskScheduler = analysisTaskScheduler;
        this.repository = repository;
    }

    public AnalysisTask trySchedule(String slug, String language, CreateAnalysisDto request) {
        if (!capabilities.isRegistered(slug, language)) {
            throw new CapabilityNotAvailableException(slug, language);
        }

        if (!analysisTaskScheduler.canSchedule()) {
            throw new QueueCapacityLimitReachedException();
        }

        AnalysisTask analysisTask = new AnalysisTask(slug, language, request.getData(), request.getRequest());
        analysisTask = this.repository.save(analysisTask);
        this.schedule(analysisTask);

        return analysisTask;
    }

    public void schedule(Task analysisTask) {
        Task task = new Task(analysisTask.getId(),
                analysisTask.getSlug(),
                analysisTask.getLanguage(),
                analysisTask.getDataConfiguration(),
                analysisTask.getRequest(),
                (finishedTask) -> {
            this.repository.save(new AnalysisTask(finishedTask));
        });

        analysisTaskScheduler.trySchedule(task);
    }

}
