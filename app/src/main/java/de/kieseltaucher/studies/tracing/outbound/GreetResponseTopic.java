package de.kieseltaucher.studies.tracing.outbound;

import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.Topic;

@ApplicationScoped
public class GreetResponseTopic {

    public static final String TOPIC_NAME = "java:/jms/topic/greet-topic";

    @Inject
    private JMSContext context;

    @Resource(lookup = TOPIC_NAME)
    private Topic topic;

    public void publish(String response) {
        context.createProducer().send(topic, response);
    }

}
