package net.ssehub.jacat.worker.analysis.queue;

import lombok.extern.slf4j.Slf4j;
import net.ssehub.jacat.api.addon.task.Task;
import net.ssehub.jacat.api.analysis.IAnalysisScheduler;
import net.ssehub.jacat.api.analysis.IAnalysisTaskExecutor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Service
@EnableScheduling
@Slf4j
public class AnalysisTaskExecutor implements IAnalysisTaskExecutor {

    public static final int MAX_RUNNING_PROCESSES = 128;

    private final IAnalysisScheduler analysisScheduler;
    private final AnalysisTaskProcessor analysisTaskProcessor;

    private Set<Task> runningTasks = Collections.synchronizedSet(new HashSet<>());

    public AnalysisTaskExecutor(IAnalysisScheduler analysisScheduler,
                                AnalysisTaskProcessor analysisTaskProcessor) {
        this.analysisScheduler = analysisScheduler;
        this.analysisTaskProcessor = analysisTaskProcessor;
    }

    @Override
    public boolean isRunning(Task task) {
        return this.runningTasks.contains(task);
    }

    @Scheduled(cron = "*/1 * * * * *")
    @Override
    public void executeTasks() {
        for (int i = 0; i < MAX_RUNNING_PROCESSES - this.runningTasks.size(); i++) {
            Task task = this.analysisScheduler.getNext();
            if (task != null) {
                this.runningTasks.add(task);
                this.log.info("Currently running tasks: " + this.runningTasks.size());
                this.analysisTaskProcessor.process(task, (result -> { // never reaching
                    // TODO: Fix this later (tomorrow?)
                    this.runningTasks.remove(task);
                    this.log.info("Currently running tasks: " + this.runningTasks.size());
                    result.finish();
                }));
            } else {
                break;
            }
        }
    }

}
