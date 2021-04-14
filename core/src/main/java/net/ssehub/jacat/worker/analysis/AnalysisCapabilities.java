package net.ssehub.jacat.worker.analysis;

import net.ssehub.jacat.api.addon.Addon;
import net.ssehub.jacat.api.addon.analysis.AbstractAnalysisCapability;
import net.ssehub.jacat.api.analysis.IAnalysisCapabilities;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class AnalysisCapabilities implements IAnalysisCapabilities {
    private Map<AbstractAnalysisCapability, Addon> capabilities = new HashMap<>();

    public void register(Addon addon, AbstractAnalysisCapability analysisCapability) {
        if (addon == null || analysisCapability == null) {
            return;
        }

        this.capabilities.put(analysisCapability, addon);
    }

    public boolean isRegistered(String slug, String language) {
        try {
            return this.getCapability(slug, language) != null;
        } catch (CapabilityNotFoundException ex) {
            return false;
        }
    }

    public AbstractAnalysisCapability getCapability(String slug, String language) {
        return this.capabilities.keySet()
            .stream()
            .filter(capability -> capability.getSlug().equalsIgnoreCase(slug))
            .filter(
                capability -> capability.getLanguages().contains(language.toLowerCase())
            )
            .findFirst()
            .orElseThrow(() -> new CapabilityNotFoundException(slug, language));
    }

    @Override
    public Addon getCapabilityHolder(String slug, String language) {
        return this.capabilities.get(this.getCapability(slug, language));
    }
}
