package de.kieseltaucher.studies.tracing.tracing.app;

public class ServiceUnavailableException extends RuntimeException {

    ServiceUnavailableException(String message) {
        super(message);
    }

}
