package net.ssehub.jacat.worker.analysis;

import net.ssehub.jacat.api.addon.task.PreparedTask;
import net.ssehub.jacat.api.addon.task.Task;

public interface ITaskPreparer {
    PreparedTask prepare(Task task);
}
