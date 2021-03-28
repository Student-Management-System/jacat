package net.ssehub.jacat.worker.analysis;

import net.ssehub.jacat.api.addon.task.PreparedTask;

public interface ITaskScrapper {
    void scrap(PreparedTask task);
}
