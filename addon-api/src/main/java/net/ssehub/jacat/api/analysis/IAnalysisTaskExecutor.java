package net.ssehub.jacat.api.analysis;

import net.ssehub.jacat.api.addon.task.Task;

public interface IAnalysisTaskExecutor {

    boolean isRunning(Task task);

    void executeTasks();

}
