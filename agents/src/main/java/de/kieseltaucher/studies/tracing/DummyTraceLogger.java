package de.kieseltaucher.studies.tracing;

import org.springframework.stereotype.Component;

import de.kieseltaucher.studies.tracing.tracing.TraceLogger;

@Component
class DummyTraceLogger implements TraceLogger {

    @Override
    public void log(String msg) {
        // its a dummy
    }

    @Override
    public void error(Exception e) {
        // its a dummy
    }
}
