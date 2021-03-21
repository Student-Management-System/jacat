package net.ssehub.jacat.worker.analysis;

import java.nio.file.Path;
import lombok.extern.slf4j.Slf4j;
import net.ssehub.jacat.api.addon.data.*;
import net.ssehub.jacat.api.addon.task.PreparedTask;
import net.ssehub.jacat.api.addon.task.Task;
import net.ssehub.jacat.worker.data.CopySubmissionVisitor;
import net.ssehub.jacat.worker.data.DataCollectors;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

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
        Path workspace = createWorkspace(task);
        SubmissionCollection collection = collectSubmissions(task);
        collection.accept(new CopySubmissionVisitor(workspace));
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

    private SubmissionCollection collectSubmissions(Task task) {
        DataSection data = task.getDataConfiguration();
        AbstractDataCollector collector =
            this.dataCollectors.getCollector(data.getProtocol());
        DataRequest dataRequest = new DataRequest(
            data.getHomework(),
            data.getSubmission()
        );
        try {
            return collector.collect(dataRequest);
        } catch (RuntimeException e) {
            throw new ResourceNotAvailableException(e);
        } finally {
            collector.cleanup(dataRequest);
        }
    }
}
