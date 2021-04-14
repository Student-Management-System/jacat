package net.ssehub.jacat.worker.data;

import lombok.extern.slf4j.Slf4j;
import net.ssehub.jacat.api.addon.data.AbstractDataCollector;
import net.ssehub.jacat.api.addon.data.DataProcessingRequest;
import net.ssehub.jacat.api.addon.data.ResourceNotAvailableException;
import net.ssehub.jacat.api.addon.data.SubmissionCollection;
import net.ssehub.jacat.api.addon.task.PreparedTask;
import net.ssehub.jacat.api.addon.task.Task;
import net.ssehub.jacat.worker.analysis.ITaskPreparer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
@Slf4j
public class TaskPreparer implements ITaskPreparer {
    private final Path workdir;
    private final DataCollectors dataCollectors;

    public TaskPreparer(@Qualifier("workdir") Path workdir,
                        DataCollectors dataCollectors) {
        this.workdir = workdir;
        this.dataCollectors = dataCollectors;
    }

    @Override
    public PreparedTask prepare(Task task) {
        DataProcessingRequest dataProcessingRequest = task.getDataProcessingRequest();

        AbstractDataCollector collector =
            this.dataCollectors.getCollector(dataProcessingRequest.getDataCollector());

        log.debug("Creating Workspace (#" + task.getId() + ")");
        Path workspace = createWorkspace(task);
        SubmissionCollection collection = null;
        try {
            log.debug("Collecting started (#" + task.getId() + ")");
            collection = collector.collect(dataProcessingRequest);
            log.debug("Collecting ended (#" + task.getId() + ")");
            log.debug("Moving started (#" + task.getId() + ")");
            collection.accept(new CopySubmissionVisitor(workspace));
            log.debug("Moving ended (#" + task.getId() + ")");
        } catch (RuntimeException e) {
            throw new ResourceNotAvailableException(e);
        } finally {
            log.debug("Cleanup Temp started (#" + task.getId() + ")");
            if (collection != null) {
//                collector.cleanup(dataProcessingRequest);
            }
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
