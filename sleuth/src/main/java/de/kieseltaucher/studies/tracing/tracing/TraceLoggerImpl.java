package de.kieseltaucher.studies.tracing.tracing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import de.kieseltaucher.studies.tracing.tracing.tracing.TraceLogger;

@Component
class TraceLoggerImpl implements TraceLogger {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void log(String msg) {
        logger.info(msg);
    }

    @Override
    public void error(Exception e) {
        logger.error(e.toString(), e);
    }
}
