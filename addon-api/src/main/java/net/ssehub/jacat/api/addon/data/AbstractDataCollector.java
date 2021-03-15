package net.ssehub.jacat.api.addon.data;

public abstract class AbstractDataCollector {

    private String protocol;

    public AbstractDataCollector(String protocol) {
        this.protocol = protocol;
    }

    public String getProtocol() {
        return protocol;
    }

    public abstract SubmissionCollection collect(DataRequest request);

    public abstract void clear(DataRequest request);
}
