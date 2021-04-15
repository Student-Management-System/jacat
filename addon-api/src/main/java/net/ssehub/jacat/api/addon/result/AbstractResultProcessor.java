package net.ssehub.jacat.api.addon.result;

import net.ssehub.jacat.api.addon.task.Task;

public abstract class AbstractResultProcessor {

    public abstract void process(Task task);

}
