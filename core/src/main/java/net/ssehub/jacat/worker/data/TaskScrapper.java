package net.ssehub.jacat.worker.data;

import lombok.extern.slf4j.Slf4j;
import net.ssehub.jacat.api.addon.task.PreparedTask;
import net.ssehub.jacat.worker.analysis.ITaskScrapper;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

@Component
@Slf4j
public class TaskScrapper implements ITaskScrapper {

    @Override
    public void scrap(PreparedTask task) {
        if (task == null || task.getWorkspace() == null) {
            return;
        }

        try {
            Files.walk(task.getWorkspace())
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
        } catch (Exception e) {
            log.error("Cannot delete workspace: " + task.getWorkspace(), e);
        }

        File workspace = task.getWorkspace().toFile();
        if (workspace.exists()) {
            workspace.delete();
        }
    }
}
