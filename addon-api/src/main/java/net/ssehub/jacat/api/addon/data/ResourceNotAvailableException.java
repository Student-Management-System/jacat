package net.ssehub.jacat.api.addon.data;

/**
 * This Exception usually gets thrown if a desired data resource is not available.
 */
public class ResourceNotAvailableException extends RuntimeException {

    /**
     * Default constructor. Sets the message of the exception.
     */
    public ResourceNotAvailableException() {
        super("Resource is not available.");
    }

    /**
     * Sets message and cause of the exception.
     *
     * @param cause the cause.
     */
    public ResourceNotAvailableException(Throwable cause) {
        super("Resource is not available.", cause);
    }
}
