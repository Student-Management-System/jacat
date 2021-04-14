package net.ssehub.jacat.worker;

import lombok.extern.slf4j.Slf4j;
import net.ssehub.jacat.api.AbstractJacatWorker;
import net.ssehub.jacat.api.addon.Addon;
import net.ssehub.jacat.api.addon.analysis.AbstractAnalysisCapability;
import net.ssehub.jacat.api.addon.data.AbstractDataCollector;
import net.ssehub.jacat.api.analysis.IAnalysisCapabilities;
import net.ssehub.jacat.platform.auth.StudMgmtClient;
import net.ssehub.jacat.worker.data.DataCollectors;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.nio.file.Path;

@Service
@Slf4j
public class JacatWorker extends AbstractJacatWorker {

    private IAnalysisCapabilities<Addon> analysisCapabilities;
    private DataCollectors dataCollectors;
    private Path workdir;

    private final StudMgmtClient studMgmtClient;

    public JacatWorker(@Qualifier("workdir") Path workdir,
                       IAnalysisCapabilities<Addon> analysisCapabilities,
                       DataCollectors dataCollectors,
                       StudMgmtClient studMgmtClient) {
        this.workdir = workdir;
        this.analysisCapabilities = analysisCapabilities;
        this.dataCollectors = dataCollectors;
        this.studMgmtClient = studMgmtClient;
    }

    public String getVersion() {
        return "1.2.3";
    }

    @Override
    public void registerAnalysisTask(Addon addon, AbstractAnalysisCapability capability) {
        for (String language : capability.getLanguages()) {
            if (this.analysisCapabilities.isRegistered(capability.getSlug(), language)) {
                throw new AnalysisCapabilityAlreadyRegisteredException(
                    capability.getSlug(),
                    language
                );
            }
        }

        this.analysisCapabilities.register(addon, capability);
    }

    @Override
    public void registerDataCollector(Addon addon, AbstractDataCollector collector) {
        if (this.dataCollectors.isRegistered(collector.getProtocol())) {
            throw new DataCollectorAlreadyRegisteredException(collector.getProtocol());
        }

        this.dataCollectors.register(addon, collector);
    }

    public StudMgmtClient getStudMgmtClient() {
        return studMgmtClient;
    }

    @Override
    public Path getWorkingDir() {
        return this.workdir;
    }

    private static class AnalysisCapabilityAlreadyRegisteredException
        extends RuntimeException {

        public AnalysisCapabilityAlreadyRegisteredException(
            String slug,
            String language
        ) {
            super(
                "The desired capability (slug=\"" +
                    slug +
                    "\", language=\"" +
                    language +
                    "\") is already registered."
            );
        }
    }

    private static class DataCollectorAlreadyRegisteredException
        extends RuntimeException {

        public DataCollectorAlreadyRegisteredException(String protocol) {
            super(
                "The desired data collector (protocol=\"" +
                    protocol +
                    "\") is already registered."
            );
        }
    }
}
