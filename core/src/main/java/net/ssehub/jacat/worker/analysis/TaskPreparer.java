package net.ssehub.jacat.worker.analysis;

import net.ssehub.jacat.api.addon.data.*;
import net.ssehub.jacat.api.addon.task.PreparedTask;
import net.ssehub.jacat.api.addon.task.Task;
import net.ssehub.jacat.worker.data.DataCollectors;
import net.ssehub.jacat.worker.data.CopySubmissionVisitor;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class TaskPreparer {

    private final DataCollectors dataCollectors;

    public TaskPreparer(DataCollectors dataCollectors) {
        this.dataCollectors = dataCollectors;
    }

    public PreparedTask prepare(Task task) {
        DataSection data = task.getDataConfiguration();
        DataRequest dataRequest = data.getRequest();

        PreparedTask preparedTask = new PreparedTask(task);
        AbstractDataCollector collector = this.dataCollectors.getCollector(data.getProtocol());
        File workspace = new File(new File(".", "debug"), "workspace");
        File taskWorkspace = new File(workspace, "tmp_" + task.getId());
        taskWorkspace.mkdirs();

        preparedTask.setWorkspace(taskWorkspace.toPath());
        SubmissionCollection collection;
        try {
            collection = collector.collect(dataRequest);
        } catch (RuntimeException e) {
            throw new ResourceNotAvailableException();
        }

        collection.accept(new CopySubmissionVisitor(taskWorkspace.toPath()));

        collector.clear(dataRequest);

        preparedTask.setSubmissions(collection);

        return preparedTask;
    }

}
