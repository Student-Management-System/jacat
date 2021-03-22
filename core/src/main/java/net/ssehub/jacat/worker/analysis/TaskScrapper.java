package net.ssehub.jacat.worker.analysis;

import lombok.extern.slf4j.Slf4j;
import net.ssehub.jacat.api.addon.task.PreparedTask;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

@Component
@Slf4j
public class TaskScrapper {

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
