package net.ssehub.jacat.worker;

import lombok.extern.slf4j.Slf4j;
import net.ssehub.jacat.api.AbstractJacatWorker;
import net.ssehub.jacat.api.addon.Addon;
import net.ssehub.jacat.api.addon.analysis.AbstractAnalysisCapability;
import net.ssehub.jacat.api.addon.data.AbstractDataCollector;
import net.ssehub.jacat.api.addon.result.AbstractResultProcessor;
import net.ssehub.jacat.api.analysis.IAnalysisCapabilities;
import net.ssehub.jacat.api.studmgmt.IStudMgmtClient;
import net.ssehub.jacat.api.studmgmt.IStudMgmtFacade;
import net.ssehub.jacat.worker.data.DataCollectors;
import net.ssehub.jacat.worker.result.ResultProcessors;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.nio.file.Path;

@Service
@Slf4j
public class JacatWorker extends AbstractJacatWorker {

    private IAnalysisCapabilities analysisCapabilities;
    private DataCollectors dataCollectors;
    private ResultProcessors resultProcessors;
    private Path workdir;

    private final IStudMgmtClient studMgmtClient;
    private final IStudMgmtFacade studMgmtFacade;

    public JacatWorker(@Qualifier("workdir") Path workdir,
                       IAnalysisCapabilities analysisCapabilities,
                       DataCollectors dataCollectors,
                       ResultProcessors resultProcessors, IStudMgmtClient studMgmtClient,
                       IStudMgmtFacade studMgmtFacade) {
        this.workdir = workdir;
        this.analysisCapabilities = analysisCapabilities;
        this.dataCollectors = dataCollectors;
        this.resultProcessors = resultProcessors;
        this.studMgmtClient = studMgmtClient;
        this.studMgmtFacade = studMgmtFacade;
    }

    public String getVersion() {
        return "1.2.3";
    }

    @Override
    public void registerAnalysisCapability(Addon addon, AbstractAnalysisCapability capability) {
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

    @Override
    public void registerResultProcessor(Addon addon, AbstractResultProcessor processor) {
        if (this.resultProcessors.isRegistered(processor)) {

        }
        this.resultProcessors.register(processor);
    }

    public IStudMgmtClient getStudMgmtClient() {
        return studMgmtClient;
    }

    @Override
    public IStudMgmtFacade getStudMgmtFacade() {
        return this.studMgmtFacade;
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

    private static class ResultProcessorAlreadyRegisteredException
        extends RuntimeException {

        public ResultProcessorAlreadyRegisteredException(Addon addon) {
            super(
                "The desired result processor (by: "
                    + addon.getDescription().getName()
                    + ") is already registered."
            );
        }
    }
}
