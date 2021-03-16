package net.ssehub.jacat.worker.analysis.queue;

import lombok.extern.slf4j.Slf4j;
import net.ssehub.jacat.api.addon.Addon;
import net.ssehub.jacat.api.addon.task.AbstractAnalysisCapability;
import net.ssehub.jacat.api.addon.task.PreparedTask;
import net.ssehub.jacat.api.addon.task.Task;
import net.ssehub.jacat.api.analysis.IAnalysisCapabilities;
import net.ssehub.jacat.api.analysis.TaskCompletion;
import net.ssehub.jacat.worker.analysis.TaskPreparer;
import net.ssehub.jacat.worker.analysis.TaskScrapper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@Slf4j
public class AnalysisTaskProcessor {

    private final IAnalysisCapabilities<Addon> capabilities;
    private final TaskPreparer taskPreparer;
    private final TaskScrapper taskScrapper;

    public AnalysisTaskProcessor(IAnalysisCapabilities<Addon> capabilities,
                                 TaskPreparer taskPreparer,
                                 TaskScrapper taskScrapper) {
        this.capabilities = capabilities;
        this.taskPreparer = taskPreparer;
        this.taskScrapper = taskScrapper;
    }


    @Async
    public void process(Task task, TaskCompletion completion) {
        AbstractAnalysisCapability capability = this.capabilities.getCapability(task.getSlug(), task.getLanguage());

        log.info("Started AnalysingTask (#" + task.getId() + "): [slug=\"" + task.getSlug()
                + "\", language=\"" + task.getLanguage() + "\"]");
        long timeStart = System.currentTimeMillis();

        PreparedTask result = new PreparedTask(task);
        try {
            PreparedTask preparedTask = this.taskPreparer.prepare(task);
            result = capability.run(preparedTask);
            this.taskScrapper.scrap(preparedTask);
        } catch(RuntimeException e) {
            result.setFailedResult(Collections.singletonMap("message", e.getMessage()));
        }

        Task.Status status = result.getStatus();
        if (status == null) {
            status = Task.Status.SUCCESSFUL;
        }

        task.setResult(status, result.getResult());
        long timeEnd = System.currentTimeMillis();
        long time = timeEnd - timeStart;

        log.info("Finished AnalysingTask (#" + result.getId() + ") in "
                + time + "ms with status [" + status + "]");
        completion.finish(task);


    }

}
