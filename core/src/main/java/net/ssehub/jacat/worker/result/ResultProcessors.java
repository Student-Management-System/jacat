package net.ssehub.jacat.worker.result;

import net.ssehub.jacat.api.addon.result.AbstractResultProcessor;
import net.ssehub.jacat.api.addon.task.Task;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ResultProcessors {

    private List<AbstractResultProcessor> processors = new ArrayList<>();

    public void register(AbstractResultProcessor processor) {
        this.processors.add(processor);
    }

    public void process(Task task) {
        processors.forEach(processor -> processor.process(task));
    }

    public boolean isRegistered(AbstractResultProcessor processor) {
        return processors.contains(processor);
    }
}
