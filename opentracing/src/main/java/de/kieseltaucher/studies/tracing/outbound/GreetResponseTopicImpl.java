package de.kieseltaucher.studies.tracing.outbound;

import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.Topic;

import org.eclipse.microprofile.opentracing.Traced;

import de.kieseltaucher.studies.tracing.inbound.GreetTopicMDB;
import de.kieseltaucher.studies.tracing.tracing.JMSContextMediator;
import de.kieseltaucher.studies.tracing.tracing.TraceLogger;

@ApplicationScoped
public class GreetResponseTopicImpl implements GreetResponseTopic {

    private static final String TOPIC_NAME = GreetTopicMDB.TOPIC_NAME;

    @Inject
    private JMSContext jmsContext;

    @Resource(lookup = TOPIC_NAME)
    private Topic topic;

    @Inject
    private JMSContextMediator jmsTracing;

    @Inject
    private TraceLogger traceLogger;

    @Traced
    @Override
    public void publish(String response) {
        traceLogger.log("Publish response to topic");
        JMSProducer producer = jmsTracing.configure(jmsContext.createProducer());
        producer.send(topic, response);
    }

}
