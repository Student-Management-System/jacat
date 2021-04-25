package net.ssehub.jacat.platform.analysis.exception;

import net.ssehub.jacat.platform.error.ApplicationRuntimeException;
import org.springframework.http.HttpStatus;

public class CourseConfigurationNotFoundException extends ApplicationRuntimeException {

    public CourseConfigurationNotFoundException() {
        super("Invalid course provided", HttpStatus.BAD_REQUEST);
    }
}
