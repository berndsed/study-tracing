package de.kieseltaucher.studies.tracing.tracing;

import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;

import io.opentracing.Tracer;
import io.opentracing.util.GlobalTracer;

@ApplicationScoped
public class TraceProducer {

    private final Logger logger = Logger.getLogger(getClass().getName());

    @RequestScoped
    @Produces
    public Tracer getTrace() {
        logger.info("Producing new tracer");
        return GlobalTracer.get();
    }
}
