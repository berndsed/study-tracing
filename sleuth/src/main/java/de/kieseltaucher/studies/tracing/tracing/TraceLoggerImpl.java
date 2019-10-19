package de.kieseltaucher.studies.tracing.tracing;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import brave.Tracer;

@Component
class TraceLoggerImpl implements TraceLogger {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Inject
    private Tracer tracer;

    @Override
    public void log(String msg) {
        tracer.currentSpanCustomizer().annotate(msg);
        logger.info(msg);
    }

    @Override
    public void error(Exception e) {
        tracer.currentSpanCustomizer().tag("error", e.toString());
        logger.error(e.toString(), e);
    }
}
