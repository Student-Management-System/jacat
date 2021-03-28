package net.ssehub.jacat.platform.analysis.exception;

import net.ssehub.jacat.platform.error.ApplicationRuntimeException;
import org.springframework.http.HttpStatus;

public class QueueCapacityLimitReachedException extends ApplicationRuntimeException {

    public QueueCapacityLimitReachedException() {
        super("Cannot queue task, capacity limit reached.", HttpStatus.BAD_REQUEST);
    }
}
