package net.ssehub.jacat.worker.analysis;

import lombok.extern.slf4j.Slf4j;
import net.ssehub.jacat.api.addon.data.*;
import net.ssehub.jacat.api.addon.task.PreparedTask;
import net.ssehub.jacat.api.addon.task.Task;
import net.ssehub.jacat.worker.data.DataCollectors;
import net.ssehub.jacat.worker.data.CopySubmissionVisitor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Path;

@Component
@Slf4j
public class TaskPreparer {

    private final Path workdir;
    private final DataCollectors dataCollectors;


    public TaskPreparer(@Qualifier("workdir") Path workdir,
                        DataCollectors dataCollectors) {
        this.workdir = workdir;
        this.dataCollectors = dataCollectors;
    }

    public PreparedTask prepare(Task task) {
        DataSection data = task.getDataConfiguration();
        DataRequest dataRequest = new DataRequest(data.getHomework(), data.getSubmission());

        PreparedTask preparedTask = new PreparedTask(task);
        AbstractDataCollector collector = this.dataCollectors.getCollector(data.getProtocol());
        File taskWorkspace = this.workdir.resolve("workspace")
                .resolve("tmp_" + task.getId()).toFile();
        taskWorkspace.mkdirs();

        preparedTask.setWorkspace(taskWorkspace.toPath());
        SubmissionCollection collection;
        try {
            collection = collector.collect(dataRequest);
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw new ResourceNotAvailableException();
        }

        collection.accept(new CopySubmissionVisitor(taskWorkspace.toPath()));
        preparedTask.setSubmissions(collection);

        collector.cleanup(dataRequest);

        return preparedTask;
    }

}
