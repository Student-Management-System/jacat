package net.ssehub.jacat.worker.analysis;

import net.ssehub.jacat.api.addon.task.Task;

public class TaskAlreadyRunningException extends RuntimeException{
    public TaskAlreadyRunningException(Task task) {
        super("Task is already running: " + task.getId());
    }
}
