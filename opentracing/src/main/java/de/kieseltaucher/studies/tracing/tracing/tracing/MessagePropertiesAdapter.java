package de.kieseltaucher.studies.tracing.tracing.tracing;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.jms.JMSException;
import javax.jms.Message;

class MessagePropertiesAdapter {

    private final Message message;

    MessagePropertiesAdapter(Message message) {
        this.message = message;
    }

    Collection<String> getKeys() {
        return getKeyValues()
            .stream()
            .map(ContextPropertyKey::fromJMSPropertyKey)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(ContextPropertyKey::getJMSPropertyKey)
            .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    private Collection<String> getKeyValues() {
        try {
            Collection<?> collection = Collections.list(message.getPropertyNames());
            return collection
                .stream()
                .map(Object::toString)
                .collect(Collectors.toList());
        } catch (JMSException e) {
            throw new JMSPropertyException(e);
        }
    }

    String getValue(String key) {
        try {
            return message.getStringProperty(key);
        } catch (JMSException e) {
            throw new JMSPropertyException(e);
        }
    }

    void put(String key, String value) {
        try {
            message.setStringProperty(key, value);
        } catch (JMSException e) {
            throw new JMSPropertyException(e);
        }
    }

    private static class JMSPropertyException extends RuntimeException {
        private JMSPropertyException(JMSException cause) {
            super(cause);
        }
    }

}
