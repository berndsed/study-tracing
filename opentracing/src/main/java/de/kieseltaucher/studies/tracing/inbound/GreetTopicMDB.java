package de.kieseltaucher.studies.tracing.inbound;

import java.util.logging.Logger;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import de.kieseltaucher.studies.tracing.tracing.JMSContextMediator;
import de.kieseltaucher.studies.tracing.tracing.TraceLogger;
import io.opentracing.Scope;

@MessageDriven(name = "GreetTopicMDB", activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = GreetTopicMDB.TOPIC_NAME),
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
})
public class GreetTopicMDB implements MessageListener {

    public static final String TOPIC_NAME = "java:/jms/topic/greet-topic";

    private final Logger logger = Logger.getLogger(getClass().getName());

    @Inject
    private TraceLogger traceLogger;

    @Inject
    private JMSContextMediator jmsTracing;

    @Override
    public void onMessage(Message message) {
        logger.info(String.format("onMessage from %s", TOPIC_NAME));
        try (Scope scope = contextFollowsFrom(message)) {
            String greetResponse = extractText(message);
            traceLogger.log(String.format("Consumed greet-response \"%s\"", greetResponse));
        } catch (RuntimeException | JMSException e) {
            traceLogger.error(e);
        }
    }

    private Scope contextFollowsFrom(Message message) {
        String name = getClass() + ".onMessage";
        return jmsTracing.followsFrom(name, message);
    }

    private String extractText(Message message) throws JMSException {
        return ((TextMessage) message).getText();
    }
}
