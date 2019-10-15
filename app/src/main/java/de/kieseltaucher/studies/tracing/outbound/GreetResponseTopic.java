package de.kieseltaucher.studies.tracing.outbound;

public interface GreetResponseTopic {
    void publish(String response);
}
