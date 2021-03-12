package net.ssehub.jacat.platform.analysis;

import net.ssehub.jacat.platform.error.ApplicationRuntimeException;
import org.springframework.http.HttpStatus;

public class AnalysisTaskNotFoundException extends ApplicationRuntimeException {
    public AnalysisTaskNotFoundException() {
        super("AnalysisTask not found.", HttpStatus.NOT_FOUND);
    }
}
