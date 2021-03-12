package net.ssehub.jacat.platform.analysis;

import net.ssehub.jacat.platform.error.ApplicationRuntimeException;
import org.springframework.http.HttpStatus;

public class CapabilityNotAvailableException extends ApplicationRuntimeException {
    public CapabilityNotAvailableException(String slug, String language) {
        super("Capability not found: " + slug + " / " + language, HttpStatus.NOT_FOUND);
    }
}
