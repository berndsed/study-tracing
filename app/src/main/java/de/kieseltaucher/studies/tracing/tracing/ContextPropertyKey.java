package de.kieseltaucher.studies.tracing.tracing;

import java.util.Objects;
import java.util.Optional;

class ContextPropertyKey {

    private static final String PREFIX = "x_trace_";
    private static final char JMS_DELIMITER = '_';
    private static final char CONTEXT_DELIMITER = '-';

    static ContextPropertyKey fromContextKey(String key) {
        return new ContextPropertyKey(key);
    }

    static Optional<ContextPropertyKey> fromJMSPropertyKey(String key) {
        if (!key.startsWith(PREFIX)) {
            return Optional.empty();
        }
        String postfix = key.substring(PREFIX.length());
        String contextKeyValue = postfix.replace(JMS_DELIMITER, CONTEXT_DELIMITER);
        return Optional.of(new ContextPropertyKey(contextKeyValue));
    }

    private final String key;

    private ContextPropertyKey(String key) {
        if (key == null) {
            throw new NullPointerException();
        }
        this.key = key;
    }

    String getContextKey() {
        return key;
    }

    String getJMSPropertyKey() {
        return (PREFIX + key).replace(CONTEXT_DELIMITER, JMS_DELIMITER);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ContextPropertyKey that = (ContextPropertyKey) o;
        return key.equals(that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }

    @Override
    public String toString() {
        return key;
    }
}
