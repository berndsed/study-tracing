package de.kieseltaucher.studies.tracing.outbound;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import de.kieseltaucher.studies.tracing.inbound.GreetTopicListener;

@Component
class GreetResponseTopicImpl implements GreetResponseTopic {

    @Autowired
    private JmsTemplate jmsTemplate;

    @Override
    public void publish(String response) {
        jmsTemplate.convertAndSend(GreetTopicListener.TOPIC_NAME, response);

    }
}
