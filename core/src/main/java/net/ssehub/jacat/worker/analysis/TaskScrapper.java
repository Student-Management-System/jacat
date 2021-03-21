package net.ssehub.jacat.worker.analysis;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import lombok.extern.slf4j.Slf4j;
import net.ssehub.jacat.api.addon.task.PreparedTask;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TaskScrapper {

    public void scrap(PreparedTask task) {
        try {
            Files
                .walk(task.getWorkspace())
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
        } catch (IOException e) {
            log.error("Cannot delete workspace: " + task.getWorkspace(), e);
        }
    }
}
