package net.ssehub.jacat.platform.analysis;

import net.ssehub.jacat.platform.error.ApplicationRuntimeException;
import org.springframework.http.HttpStatus;

public class CourseConfigurationNotFoundException extends ApplicationRuntimeException {
    public CourseConfigurationNotFoundException() {
        super("Cannot find course configuration.", HttpStatus.NOT_FOUND);
    }
}
