package de.kieseltaucher.studies.tracing.tracing;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.jms.JMSProducer;
import javax.jms.Message;

import io.opentracing.propagation.TextMap;

class JMSContextCarrier implements TextMap {

    private final BiConsumer<String, String> put;
    private final Supplier<Collection<String>> keySupplier;
    private final Function<String, String> get;

    JMSContextCarrier(JMSProducer jmsProducer) {
        this(jmsProducer::setProperty, jmsProducer::getPropertyNames, jmsProducer::getStringProperty);
    }

    JMSContextCarrier(Message message) {
        this(new MessagePropertiesAdapter(message));
    }

    private JMSContextCarrier(MessagePropertiesAdapter messageProperties) {
        this(messageProperties::put, messageProperties::getKeys, messageProperties::getValue);
    }

    JMSContextCarrier(BiConsumer<String, String> put,
                      Supplier<Collection<String>> keySupplier,
                      Function<String, String> get) {
        this.put = put;
        this.keySupplier = keySupplier;
        this.get = get;
    }

    @Override
    public Iterator<Map.Entry<String, String>> iterator() {
        return keySupplier.get().stream()
            .map(this::toPropertyEntry)
            .filter(Objects::nonNull)
            .collect(Collectors.toList())
            .iterator();
    }

    private Map.Entry<String, String> toPropertyEntry(String key) {
        return ContextPropertyKey.fromJMSPropertyKey(key)
            .map(this::toPropertyEntry)
            .orElse(null);
    }

    private Map.Entry<String, String> toPropertyEntry(ContextPropertyKey key) {
        final String value = get.apply(key.getJMSPropertyKey());
        return value != null ? new AbstractMap.SimpleEntry<>(key.getContextKey(), value) : null;
    }

    @Override
    public void put(String key, String value) {
        put(ContextPropertyKey.fromContextKey(key), value);
    }

    private void put(ContextPropertyKey key, String value) {
        put.accept(key.getJMSPropertyKey(), value);
    }
}
