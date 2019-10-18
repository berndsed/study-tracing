package de.kieseltaucher.studies.tracing.tracing.tracing;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import org.eclipse.microprofile.opentracing.ClientTracingRegistrar;

@ApplicationScoped
class TracingJaxRSClientProducer {

    private ClientBuilder clientBuilder;

    @PostConstruct
    void init() {
        this.clientBuilder = ClientTracingRegistrar.configure(ClientBuilder.newBuilder());
    }

    @Produces
    @Dependent
    Client newClient() {
        return clientBuilder.build();
    }

}
