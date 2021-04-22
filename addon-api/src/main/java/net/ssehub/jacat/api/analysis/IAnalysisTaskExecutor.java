package net.ssehub.jacat.api.analysis;

import net.ssehub.jacat.api.addon.task.Task;

import java.util.concurrent.CompletableFuture;

public interface IAnalysisTaskExecutor {
    boolean isRunning(Task task);

    CompletableFuture<Task> process(Task task, TaskCompletion completion);
}
