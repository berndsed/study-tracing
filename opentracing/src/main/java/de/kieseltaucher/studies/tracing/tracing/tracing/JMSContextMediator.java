package de.kieseltaucher.studies.tracing.tracing.tracing;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.jms.JMSProducer;
import javax.jms.Message;

import io.opentracing.References;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import io.opentracing.propagation.TextMap;

@Dependent
public class JMSContextMediator {

    @Inject
    private Tracer tracer;

    public JMSProducer configure(JMSProducer producer) {
        inject(new JMSContextCarrier(producer));
        return producer;
    }

    public Message configure(Message message) {
        inject(new JMSContextCarrier(message));
        return message;
    }

    private void inject(TextMap carrier) {
        tracer.inject(tracer.activeSpan().context(),
                      Format.Builtin.TEXT_MAP,
                      carrier);
    }

    public Scope followsFrom(String spanName, Message message) {
        SpanContext sendersContext = extract(new JMSContextCarrier(message));
        return buildFollowsFrom(spanName, sendersContext);
    }

    private SpanContext extract(TextMap context) {
        return tracer.extract(
            Format.Builtin.TEXT_MAP,
            context
        );
    }
    private Scope buildFollowsFrom(String spanName, SpanContext from) {
        Span span = tracer
            .buildSpan(spanName)
            .addReference(References.FOLLOWS_FROM, from)
            .start();
        return tracer.scopeManager().activate(span, true);
    }

}
