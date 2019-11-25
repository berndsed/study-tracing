package de.kieseltaucher.studies.tracing.inbound;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class GreetTopicListener {

    public static final String TOPIC_NAME = "greet-topic";

    private final Logger log = LoggerFactory.getLogger(getClass());

    @JmsListener(destination = TOPIC_NAME)
    public void receiveMessage(String response) {
        log.info("Received response {}", response);
    }

}
