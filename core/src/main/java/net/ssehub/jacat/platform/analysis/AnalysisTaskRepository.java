package net.ssehub.jacat.platform.analysis;

import net.ssehub.jacat.api.addon.task.Task;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Profile("!local")
@Repository
public interface AnalysisTaskRepository extends MongoRepository<AnalysisTask, String> {

    List<AnalysisTask> findAllByStatus(Task.Status status);

}
