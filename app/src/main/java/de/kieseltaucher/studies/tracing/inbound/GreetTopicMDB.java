package de.kieseltaucher.studies.tracing.inbound;

import java.util.logging.Logger;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.eclipse.microprofile.opentracing.Traced;

import de.kieseltaucher.studies.tracing.outbound.GreetResponseTopic;
import de.kieseltaucher.studies.tracing.tracing.TraceLogger;

@MessageDriven(name = "GreetTopicMDB", activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = GreetResponseTopic.TOPIC_NAME),
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
})
@Traced
public class GreetTopicMDB implements MessageListener {

    private final Logger logger = Logger.getLogger(getClass().getName());

    @Inject
    private TraceLogger traceLogger;

    @Override
    public void onMessage(Message message) {
        logger.info(String.format("onMessage from %s", GreetResponseTopic.TOPIC_NAME));
        String greetResponse = extractText(message);
        traceLogger.log(String.format("Consumed greet-response \"%s\"", greetResponse));
    }

    private String extractText(Message message) {
        try {
            return ((TextMessage) message).getText();
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }
}
