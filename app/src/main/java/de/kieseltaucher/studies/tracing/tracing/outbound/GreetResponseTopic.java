package de.kieseltaucher.studies.tracing.tracing.outbound;

public interface GreetResponseTopic {
    void publish(String response);
}
