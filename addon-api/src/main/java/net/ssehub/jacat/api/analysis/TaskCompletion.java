package net.ssehub.jacat.api.analysis;

import net.ssehub.jacat.api.addon.task.Task;

public interface TaskCompletion {
    void finish(Task result);
}
