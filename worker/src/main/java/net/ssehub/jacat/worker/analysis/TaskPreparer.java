package net.ssehub.jacat.worker.analysis;

import net.ssehub.jacat.api.addon.data.*;
import net.ssehub.jacat.api.addon.task.PreparedTask;
import net.ssehub.jacat.api.addon.task.Task;
import net.ssehub.jacat.worker.data.DataCollectors;
import net.ssehub.jacat.api.addon.data.DataRequest;
import net.ssehub.jacat.worker.data.MoveSubmissionVisitor;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class TaskPreparer {

    private DataCollectors dataCollectors;

    public TaskPreparer(DataCollectors dataCollectors) {
        this.dataCollectors = dataCollectors;
    }

    public PreparedTask prepare(Task task) {
        DataSection data = task.getDataConfiguration();
        DataRequest dataRequest = new DataRequest(data.getCourse(), data.getHomework(), data.getSubmission());

        PreparedTask preparedTask = new PreparedTask(task);
        AbstractDataCollector collector = this.dataCollectors.getCollector(data.getProtocol());
        File workspace = new File(new File(".", "debug"), "workspace");
        File taskWorkspace = new File(workspace, "tmp_" + task.getId());
        taskWorkspace.mkdirs();

        preparedTask.setWorkspace(taskWorkspace.toPath());
        SubmissionCollection collection;
        try {
            collector.arrange(dataRequest);
            collection = collector.collect(dataRequest);
        } catch (RuntimeException e) {
            throw new ResourceNotAvailableException();
        }

        collection.accept(new MoveSubmissionVisitor(taskWorkspace.toPath()));
        // TODO: Check if created Files are in Folder
        preparedTask.setSubmissions(collection);

        return preparedTask;
    }

}
