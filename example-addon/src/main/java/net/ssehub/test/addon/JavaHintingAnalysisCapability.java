package net.ssehub.test.addon;

import net.ssehub.jacat.api.addon.task.AbstractAnalysisCapability;
import net.ssehub.jacat.api.addon.task.PreparedTask;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class JavaHintingAnalysisCapability extends AbstractAnalysisCapability {

    public JavaHintingAnalysisCapability() {
        super("hinting", Arrays.asList("Java"), 1.0);
    }

    @Override
    public PreparedTask run(PreparedTask task) {
        System.out.println("New Task");
        System.out.println(task.getRequest());



        Map<String, Object> request = task.getRequest();
        Map<String, Object> response = new HashMap<>();
        request.keySet().forEach(key -> {
            response.put(key.toUpperCase(), request.get(key));
        });

        task.setSuccessfulResult(response);
        return task;
    }

}
