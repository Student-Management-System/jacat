package net.ssehub.jacat.api.addon.data;

/**
 * The {@link AbstractDataCollector} class describes a
 * DataCollector of an addon. This can be registered via
 * {@link net.ssehub.jacat.api.AbstractJacatWorker}. A
 * DataCollector always consists of a 'collect' and
 * 'cleanup' phase. The 'collect' phase is responsible
 * for indexing the data. Here files are provided in the
 * local file system and returned using the
 * {@link SubmissionCollection}. An analysis capability
 * builds on this data. The 'cleanup' phase on the other
 * hand, is responsible for cleaning up the data if this
 * DataCollector has downloaded files.
 */
public abstract class AbstractDataCollector {
    private final String protocol;

    /**
     * Each DataCollector must define with which
     * protocol it works with. This is completely
     * up to the DataCollector. completely free. He can
     * freely determine what the protocol can be called.
     * Under certain circumstances, a DataCollector only
     * fits for one course and therefore has a dependency
     * on the course itself, since it is a specific
     * structure of files. a specific structure of the
     * files. It has been good practice to use the following
     * format:
     * <b>technology-course</b>
     *
     * @param protocol the protocol name, choose freely
     */
    public AbstractDataCollector(String protocol) {
        this.protocol = protocol;
    }

    /**
     * Getter for the protocol.
     *
     * @return the underlying protocol
     */
    public final String getProtocol() {
        return protocol;
    }

    /**
     * This method represents the core of a DataCollector.
     * It must be implemented so that the DataCollector can
     * index the data correctly. As parameter you get a
     * {@link DataProcessingRequest} passed, so that you can filter
     * and index the data accordingly.
     *
     * @param request The DataRequest-Information, which
     *                contains information about the desired
     *                homework and submissions the datacollector
     *                should collect.
     * @return a {@link SubmissionCollection} containing all
     * collected {@link Submission}s.
     */
    public abstract SubmissionCollection collect(DataProcessingRequest request);

    /**
     * After successful processing of the data, the original
     * data must be removed. This method must be implemented
     * when the DataCollector has collected data and it must
     * be removed.
     *
     * @param request The DataRequest-Information, which
     *                contains information about the desired
     *                homework and submissions the datacollector
     *                should cleanup.
     */
    public abstract void cleanup(DataProcessingRequest request);
}
