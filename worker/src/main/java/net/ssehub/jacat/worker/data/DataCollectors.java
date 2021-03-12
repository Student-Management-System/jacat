package net.ssehub.jacat.worker.data;

import net.ssehub.jacat.api.addon.Addon;
import net.ssehub.jacat.api.addon.data.AbstractDataCollector;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class DataCollectors {

    private Map<AbstractDataCollector, Addon> collectors = new ConcurrentHashMap<>();

    public void register(Addon addon, AbstractDataCollector collector) {
        this.collectors.put(collector, addon);
    }

    public Map<AbstractDataCollector, Addon> getCollectors() {
        return collectors;
    }

    public boolean isRegistered(String protocol) {
        try {
            getCollector(protocol);
        } catch (DataCollectorNotFoundException e) {
            return false;
        }
        return true;
    }

    public AbstractDataCollector getCollector(String protocol) {
        return this.collectors.keySet()
                .stream()
                .filter(collector -> collector.getProtocol().equalsIgnoreCase(protocol))
                .findFirst()
                .orElseThrow(() -> new DataCollectorNotFoundException(protocol));
    }

    public static class DataCollectorNotFoundException extends RuntimeException {
        public DataCollectorNotFoundException(String protocol) {
            super("The desired collector (protocol=\"" +
                    protocol + "\") could not be found.");
        }
    }
}
