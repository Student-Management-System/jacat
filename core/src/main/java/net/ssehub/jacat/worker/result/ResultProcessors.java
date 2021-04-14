package net.ssehub.jacat.worker.result;

import net.ssehub.jacat.api.addon.result.AbstractResultProcessor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class ResultProcessors {

    private List<AbstractResultProcessor> processors = new ArrayList<>();

    public void register(AbstractResultProcessor processor) {
        this.processors.add(processor);
    }

    public void process(Map<String, Object> result) {
        processors.forEach(processor -> processor.process(result));
    }

    public boolean isRegistered(AbstractResultProcessor processor) {
        return processors.contains(processor);
    }
}
