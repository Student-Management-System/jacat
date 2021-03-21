package net.ssehub.jacat.api.addon.data;

public abstract class AbstractDataCollector {
    private final String protocol;

    public AbstractDataCollector(String protocol) {
        this.protocol = protocol;
    }

    public String getProtocol() {
        return protocol;
    }

    public abstract SubmissionCollection collect(DataRequest request);

    public abstract void cleanup(DataRequest request);
}
