package net.ssehub.jacat.api.addon.data;

public class ResourceNotAvailableException extends RuntimeException {
    public ResourceNotAvailableException() {
        super("Resource is not available.");
    }

    public ResourceNotAvailableException(Throwable e) {
        super("Resource is not available.", e);
    }
}
