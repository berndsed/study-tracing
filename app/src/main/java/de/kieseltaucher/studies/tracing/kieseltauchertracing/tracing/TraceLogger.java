package de.kieseltaucher.studies.tracing.kieseltauchertracing.tracing;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import io.opentracing.Span;
import io.opentracing.Tracer;

@Dependent
public class TraceLogger {

    private static final Span NOOP_SPAN = new NoopSpan();

    @Inject
    private Tracer tracer;
    private final Logger logger = Logger.getLogger(getClass().getName());

    public void log(String msg) {
        logger.log(Level.INFO, msg);
        activeSpanOrNoop().log(msg);
    }

    private Span activeSpanOrNoop() {
        final Span activeSpan = tracer.activeSpan();
        if (activeSpan == null) {
            logger.warning("No active span");
        }
        return activeSpan != null ? activeSpan : NOOP_SPAN;
    }
}
