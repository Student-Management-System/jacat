package net.ssehub.jacat.api.analysis;

import net.ssehub.jacat.api.addon.task.AbstractAnalysisCapability;

public interface IAnalysisCapabilities<T> {

    void register(T analyzer, AbstractAnalysisCapability analysisCapability);

    boolean isRegistered(String slug, String language);

    AbstractAnalysisCapability getCapability(String slug, String language);

    T getCapabilityHolder(String slug, String language);

    class CapabilityNotFoundException extends RuntimeException {
        public CapabilityNotFoundException(String slug, String language) {
            super("The desired capability (slug=\"" +
                    slug + "\", language=\"" + language +
                    "\" could not be found.");
        }
    }
}
