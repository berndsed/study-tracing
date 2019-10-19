package de.kieseltaucher.studies.tracing.tracing;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import io.opentracing.Span;
import io.opentracing.Tracer;

@Dependent
public class TraceLoggerImpl implements TraceLogger {

    private static final Span NOOP_SPAN = new NoopSpan();

    @Inject
    private Tracer tracer;
    private final Logger logger = Logger.getLogger(getClass().getName());

    @Override
    public void log(String msg) {
        logger.log(Level.INFO, msg);
        activeSpanOrNoop().log(msg);
    }

    @Override
    public void error(Exception e) {
        logger.log(Level.INFO, e.toString(), e);
        Map<String, String> fields = new HashMap<>();
        fields.put("event", "error");
        fields.put("error.object", e.toString());
        activeSpanOrNoop().log(fields);
    }

    private Span activeSpanOrNoop() {
        final Span activeSpan = tracer.activeSpan();
        if (activeSpan == null) {
            logger.warning("No active span");
        }
        return activeSpan != null ? activeSpan : NOOP_SPAN;
    }
}
