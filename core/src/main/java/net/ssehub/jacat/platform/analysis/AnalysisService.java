package net.ssehub.jacat.platform.analysis;

import net.ssehub.jacat.api.addon.Addon;
import net.ssehub.jacat.api.addon.task.Task;
import net.ssehub.jacat.api.analysis.IAnalysisCapabilities;
import net.ssehub.jacat.api.analysis.IAnalysisTaskExecutor;
import net.ssehub.jacat.platform.analysis.api.CreateAnalysisDto;
import net.ssehub.jacat.platform.analysis.exception.CapabilityNotAvailableException;
import org.springframework.stereotype.Service;

import java.util.concurrent.RejectedExecutionException;

@Service
public class AnalysisService {

    private final IAnalysisCapabilities<Addon> capabilities;
    private final AnalysisTaskRepository repository;
    private final IAnalysisTaskExecutor taskExecutor;

    public AnalysisService(IAnalysisCapabilities<Addon> capabilities,
                           AnalysisTaskRepository repository,
                           IAnalysisTaskExecutor taskExecutor) {
        this.capabilities = capabilities;
        this.repository = repository;
        this.taskExecutor = taskExecutor;
    }

    public AnalysisTask tryProcess(CreateAnalysisDto request) {
        String slug = request.getData().getAnalysisSlug();
        String language = request.getData().getCodeLanguage();

        if (!capabilities.isRegistered(slug, language)) {
            throw new CapabilityNotAvailableException(slug, language);
        }

        AnalysisTask analysisTask = new AnalysisTask(
            request.getData().clone(),
            request.getRequest()
        );

        analysisTask = this.repository.save(analysisTask);
        this.process(analysisTask);

        return analysisTask;
    }

    public void process(Task analysisTask) {
        Task task = new Task(
            analysisTask.getId(),
            analysisTask.getStatus(),
            analysisTask.getDataProcessingRequest().clone(),
            analysisTask.getRequest()
        );

        try {
            this.taskExecutor.process(task,
                (finishedTask) -> this.repository.save(new AnalysisTask(finishedTask)));
        } catch (RejectedExecutionException ex) {
            // TODO: what to do, if thread pool cant handle a new task?
        }

    }
}
