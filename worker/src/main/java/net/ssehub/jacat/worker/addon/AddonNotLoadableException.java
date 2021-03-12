package net.ssehub.jacat.worker.addon;

public class AddonNotLoadableException extends RuntimeException {

    public AddonNotLoadableException(Throwable cause) {
        super("Could not load addon!", cause);
    }
}
