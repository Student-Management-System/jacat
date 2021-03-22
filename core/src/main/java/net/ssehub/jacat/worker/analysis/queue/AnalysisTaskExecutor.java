package net.ssehub.jacat.worker.analysis.queue;

import lombok.extern.slf4j.Slf4j;
import net.ssehub.jacat.api.addon.Addon;
import net.ssehub.jacat.api.addon.task.AbstractAnalysisCapability;
import net.ssehub.jacat.api.addon.task.PreparedTask;
import net.ssehub.jacat.api.addon.task.Task;
import net.ssehub.jacat.api.analysis.IAnalysisCapabilities;
import net.ssehub.jacat.api.analysis.IAnalysisTaskExecutor;
import net.ssehub.jacat.api.analysis.TaskCompletion;
import net.ssehub.jacat.worker.analysis.TaskPreparer;
import net.ssehub.jacat.worker.analysis.TaskScrapper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Service
@Slf4j
public class AnalysisTaskExecutor implements IAnalysisTaskExecutor {
    private final IAnalysisCapabilities<Addon> capabilities;
    private final TaskPreparer taskPreparer;
    private final TaskScrapper taskScrapper;

    private Set<Task> runningTasks = Collections.synchronizedSet(new HashSet<>());

    public AnalysisTaskExecutor(IAnalysisCapabilities<Addon> capabilities,
                                TaskPreparer taskPreparer,
                                TaskScrapper taskScrapper) {
        this.capabilities = capabilities;
        this.taskPreparer = taskPreparer;
        this.taskScrapper = taskScrapper;
    }

    @Override
    public boolean isRunning(Task task) {
        return this.runningTasks.contains(task);
    }

    @Async
    public void process(Task task, TaskCompletion completion) {
        if (this.runningTasks.contains(task)) {
            return;
        }

        runningTasks.add(task);

        log.info("Currently running tasks: " + this.runningTasks.size());
        AbstractAnalysisCapability capability =
            this.capabilities.getCapability(task.getSlug(), task.getLanguage());

        log.info("Started AnalysingTask (#" + task.getId() + "): [slug=\""
            + task.getSlug() + "\", language=\"" + task.getLanguage() + "\"]");
        long timeStart = System.currentTimeMillis();

        PreparedTask result = new PreparedTask(task, null, null);
        try {
            log.debug("Preparing Task (#" + task.getId() + ")");
            result = this.taskPreparer.prepare(task);
            log.debug("Analyzing Task (#" + task.getId() + ")");
            result = capability.run(result);
        } catch (RuntimeException e) {
            result.setFailedResult(Collections.singletonMap("message", e.getMessage()));
        } finally {
            log.debug("Scrapping Task (#" + task.getId() + ")");
            this.taskScrapper.scrap(result);
        }

        Task.Status status = result.getStatus();
        if (status == null) {
            status = Task.Status.SUCCESSFUL;
        }

        task.setResult(status, result.getResult());
        long timeEnd = System.currentTimeMillis();
        long time = timeEnd - timeStart;

        this.runningTasks.remove(task);
        log.info("Finished AnalysingTask (#" + result.getId() + ") in "
            + time + "ms with status [" + status + "]");
        log.info("Currently running tasks: " + this.runningTasks.size());

        completion.finish(task);
    }
}
