package net.ssehub.jacat.platform.analysis;

import net.ssehub.jacat.api.addon.task.Task;
import net.ssehub.jacat.api.addon.task.TaskMode;
import net.ssehub.jacat.api.analysis.IAnalysisCapabilities;
import net.ssehub.jacat.api.analysis.IAnalysisTaskExecutor;
import net.ssehub.jacat.platform.analysis.api.CreateAnalysisDto;
import net.ssehub.jacat.platform.analysis.exception.CapabilityNotAvailableException;
import org.springframework.stereotype.Service;

import java.util.concurrent.*;

@Service
public class AnalysisService {

    private final IAnalysisCapabilities capabilities;
    private final AnalysisTaskRepository repository;
    private final IAnalysisTaskExecutor taskExecutor;

    public AnalysisService(IAnalysisCapabilities capabilities,
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
            request.getRequest(),
            request.getMode()
        );

        analysisTask = this.repository.save(analysisTask);
        Task processedTask = this.process(analysisTask);

        return this.repository.findById(processedTask.getId()).get();
    }

    public Task process(Task analysisTask) {
        Task task = new Task(
            analysisTask.getId(),
            analysisTask.getStatus(),
            analysisTask.getDataProcessingRequest().clone(),
            analysisTask.getRequest(),
            analysisTask.getMode()
        );

        try {
            CompletableFuture<Task> process = this.taskExecutor.process(task,
                (finishedTask) -> this.repository.save(new AnalysisTask(finishedTask)));

            if (analysisTask.getMode().equals(TaskMode.SYNC)) {
                return process.get(10, TimeUnit.SECONDS); // TODO: Timeout konfigurierbar machen
            } else if (analysisTask.getMode().equals(TaskMode.ASYNC)) {
                return task;
            }
        } catch (RejectedExecutionException
            | InterruptedException
            | ExecutionException
            | TimeoutException ex) {
            // Eventuell aufsplitten (vorallem: RejectedExecutionException)
        }
        return task;
    }
}
