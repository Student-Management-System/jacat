package net.ssehub.jacat.platform.analysis;

import java.util.List;
import net.ssehub.jacat.api.addon.task.Task;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Profile("!local")
@Repository
public interface AnalysisTaskRepository extends MongoRepository<AnalysisTask, String> {
    List<AnalysisTask> findAllByStatus(Task.Status status);
}
