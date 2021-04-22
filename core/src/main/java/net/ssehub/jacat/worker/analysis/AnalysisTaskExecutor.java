package net.ssehub.jacat.worker.analysis;

import lombok.extern.slf4j.Slf4j;
import net.ssehub.jacat.api.addon.analysis.AbstractAnalysisCapability;
import net.ssehub.jacat.api.addon.data.DataProcessingRequest;
import net.ssehub.jacat.api.addon.task.FinishedTask;
import net.ssehub.jacat.api.addon.task.PreparedTask;
import net.ssehub.jacat.api.addon.task.Task;
import net.ssehub.jacat.api.analysis.IAnalysisCapabilities;
import net.ssehub.jacat.api.analysis.IAnalysisTaskExecutor;
import net.ssehub.jacat.api.analysis.TaskCompletion;
import net.ssehub.jacat.worker.result.ResultProcessors;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class AnalysisTaskExecutor implements IAnalysisTaskExecutor {
    private final IAnalysisCapabilities capabilities;
    private final ITaskPreparer taskPreparer;
    private final ITaskScrapper taskScrapper;
    private final ResultProcessors resultProcessors;

    private Set<Task> runningTasks = Collections.synchronizedSet(new HashSet<>());

    public AnalysisTaskExecutor(IAnalysisCapabilities capabilities,
                                ITaskPreparer taskPreparer,
                                ITaskScrapper taskScrapper,
                                ResultProcessors resultProcessors) {
        this.capabilities = capabilities;
        this.taskPreparer = taskPreparer;
        this.taskScrapper = taskScrapper;
        this.resultProcessors = resultProcessors;
    }

    @Override
    public boolean isRunning(Task task) {
        return this.runningTasks.contains(task);
    }

    @Async
    public CompletableFuture<Task> process(Task task, TaskCompletion completion) {
        if (this.isRunning(task)) {
            return CompletableFuture.failedFuture(new TaskAlreadyRunningException(task));
        }
        long timeStart = System.currentTimeMillis();
        this.runningTasks.add(task);

        log.debug("Currently running tasks: " + this.runningTasks.size());

        DataProcessingRequest dataProcessingReq = task.getDataProcessingRequest();
        AbstractAnalysisCapability capability =
            this.capabilities.getCapability(dataProcessingReq.getAnalysisSlug(),
                dataProcessingReq.getCodeLanguage());

        log.info("Started AnalysingTask (#" + task.getId() + "): "
            + "[slug=\"" + dataProcessingReq.getAnalysisSlug()
            + "\", language=\"" + dataProcessingReq.getCodeLanguage() + "\"]");
        
        PreparedTask preparedTask = new PreparedTask(task, null, null);
        FinishedTask result;
        try {
            log.debug("Preparing Task (#" + task.getId() + ")");
            preparedTask = this.taskPreparer.prepare(task);
            log.debug("Preparing Task ends (#" + task.getId() + ")");
            log.debug("Analyzing Task (#" + task.getId() + ")");
            long analyzingTime = System.currentTimeMillis();
            result = capability.run(preparedTask);
            analyzingTime = System.currentTimeMillis() - analyzingTime;

            log.debug("Analyzing (#" + task.getId() + ") finished in " + analyzingTime + "MS");
        } catch (RuntimeException e) {
            result = preparedTask.fail(e);
        }

        log.debug("Scrapping Task (#" + task.getId() + ")");
        this.taskScrapper.scrap(preparedTask);

        Task.Status status = result.getStatus();
        if (status == null) {
            status = Task.Status.SUCCESSFUL;
        }

        task.setResult(status, result.getResult());

        this.resultProcessors.process(task);

        long timeEnd = System.currentTimeMillis();
        long time = timeEnd - timeStart;

        this.runningTasks.remove(task);
        log.info("Finished AnalysingTask (#" + result.getId() + ") in "
            + time + "ms with status [" + status + "]");
        log.debug("Currently running tasks: " + this.runningTasks.size());

        completion.finish(task);

        return CompletableFuture.completedFuture(task);
    }
}
