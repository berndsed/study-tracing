package de.kieseltaucher.studies.tracing.tracing;

import static org.hamcrest.MatcherAssert.assertThat;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JMSContextCarrierTest {

    private JMSContextCarrier carrier;
    private Map<String, String> jmsProperties;

    @BeforeEach
    void setUp() {
        this.jmsProperties = new HashMap<>();
        this.carrier = new JMSContextCarrier(jmsProperties::put, jmsProperties::keySet, jmsProperties::get);
    }

    @Test
    void inject() {
        carrier.put("the-key", "the-value");
        assertThat(jmsProperties.entrySet(), Matchers.contains(anEntry("x_trace_the_key", "the-value")));
    }

    @Test
    void extract() {
        jmsProperties.put("x_trace_the_key", "the-value");
        jmsProperties.put("other_property", "the-value-to-ignore");

        final Iterable<Map.Entry<String, String>> extractedEntries = () -> carrier.iterator();
        assertThat(extractedEntries, Matchers.contains(anEntry("the-key", "the-value")));
    }

    private AbstractMap.Entry<String, String> anEntry(String key, String value) {
        return new AbstractMap.SimpleEntry<>(key, value);
    }
}
