package de.kieseltaucher.studies.tracing.tracing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Optional;

import org.junit.jupiter.api.Test;

class ContextPropertKeyTest {

    @Test
    void leavesContextKeyUntouched() {
        ContextPropertyKey key = ContextPropertyKey.fromContextKey("a-b.c");
        assertEquals("a-b.c", key.getContextKey());
    }

    @Test
    void fromContextKey() {
        ContextPropertyKey key = ContextPropertyKey.fromContextKey("a-b");
        assertEquals("x_trace_a_b", key.getJMSPropertyKey());
    }

    @Test
    void fromJMSPropertyKey() {
        Optional<ContextPropertyKey> key = ContextPropertyKey.fromJMSPropertyKey("x_trace_a_b");
        assertEquals("a-b", key.map(ContextPropertyKey::getContextKey).orElse(null));
    }

    @Test
    void leavesJMSPropertyKeyUntouched() {
        Optional<ContextPropertyKey> key = ContextPropertyKey.fromJMSPropertyKey("x_trace_a_b");
        assertEquals("x_trace_a_b", key.map(ContextPropertyKey::getJMSPropertyKey).orElse(null));
    }

    @Test
    void ignoresOtherJMSPropertyKeys() {
        assertFalse(ContextPropertyKey.fromJMSPropertyKey("a_b").isPresent());
    }

    @Test
    void isSymmetrical() {
        ContextPropertyKey key1 = ContextPropertyKey.fromContextKey("a-b");
        ContextPropertyKey key2 = ContextPropertyKey.fromJMSPropertyKey(key1.getJMSPropertyKey()).orElse(null);
        assertEquals(key1, key2);
    }

}
