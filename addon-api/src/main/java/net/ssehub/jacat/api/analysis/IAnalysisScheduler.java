package net.ssehub.jacat.api.analysis;

import net.ssehub.jacat.api.addon.task.Task;

public interface IAnalysisScheduler {

    boolean isScheduled(Task task);

    boolean canSchedule();

    boolean trySchedule(Task task);

    Task getNext();

}
