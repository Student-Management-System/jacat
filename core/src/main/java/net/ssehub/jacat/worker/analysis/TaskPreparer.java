package net.ssehub.jacat.worker.analysis;

import lombok.extern.slf4j.Slf4j;
import net.ssehub.jacat.api.addon.data.*;
import net.ssehub.jacat.api.addon.task.PreparedTask;
import net.ssehub.jacat.api.addon.task.Task;
import net.ssehub.jacat.worker.data.CopySubmissionVisitor;
import net.ssehub.jacat.worker.data.DataCollectors;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
@Slf4j
public class TaskPreparer {
    private final Path workdir;
    private final DataCollectors dataCollectors;

    public TaskPreparer(
        @Qualifier("workdir") Path workdir,
        DataCollectors dataCollectors
    ) {
        this.workdir = workdir;
        this.dataCollectors = dataCollectors;
    }

    public PreparedTask prepare(Task task) {
        DataSection data = task.getDataConfiguration();
        DataRequest dataRequest = new DataRequest(data.getHomework(), data.getSubmission());

        AbstractDataCollector collector =
            this.dataCollectors.getCollector(data.getProtocol());

        log.debug("Creating Workspace (#" + task.getId() + ")");
        Path workspace = createWorkspace(task);
        SubmissionCollection collection;
        try {
            log.debug("Collecting started (#" + task.getId() + ")");
            collection = collector.collect(dataRequest);
            log.debug("Collecting ended (#" + task.getId() + ")");
            log.debug("Moving started (#" + task.getId() + ")");
            collection.accept(new CopySubmissionVisitor(workspace));
            log.debug("Moving ended (#" + task.getId() + ")");
        } catch (RuntimeException e) {
            throw new ResourceNotAvailableException(e);
        } finally {
            log.debug("Cleanup Temp started (#" + task.getId() + ")");
            collector.cleanup(dataRequest);
            log.debug("Cleanup Temp ended (#" + task.getId() + ")");
        }

        return new PreparedTask(task, workspace, collection);
    }

    private Path createWorkspace(Task task) {
        Path taskWorkspace =
            this.workdir.resolve("workspace")
                .resolve("tmp_" + task.getId())
                .toAbsolutePath();
        taskWorkspace.toFile().mkdirs();
        return taskWorkspace;
    }
}
