package net.ssehub.jacat.api.analysis;

import net.ssehub.jacat.api.addon.Addon;
import net.ssehub.jacat.api.addon.analysis.AbstractAnalysisCapability;

public interface IAnalysisCapabilities {
    void register(Addon analyzer, AbstractAnalysisCapability analysisCapability);

    boolean isRegistered(String slug, String language);

    AbstractAnalysisCapability getCapability(String slug, String language);

    Addon getCapabilityHolder(String slug, String language);

    class CapabilityNotFoundException extends RuntimeException {

        public CapabilityNotFoundException(String slug, String language) {
            super(
                "The desired capability (slug=\"" +
                    slug +
                    "\", language=\"" +
                    language +
                    "\" could not be found."
            );
        }
    }
}
