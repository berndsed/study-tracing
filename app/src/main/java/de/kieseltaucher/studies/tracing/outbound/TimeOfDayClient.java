package de.kieseltaucher.studies.tracing.outbound;

import java.util.logging.Logger;

import javax.enterprise.context.Dependent;
import javax.ws.rs.ServiceUnavailableException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;

import de.kieseltaucher.studies.tracing.domain.TimeOfDay;

@Dependent
public class TimeOfDayClient {

    private final Logger logger = Logger.getLogger(getClass().getName());

    public TimeOfDay getTimeOfDay() {
        final Client client = ClientBuilder.newClient();
        try {
            return client.target("http://localhost:8080")
                .path("/time-of-day")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get(TimeOfDay.class);
        } catch (ServiceUnavailableException e) {
            logger.warning("Called time-of-day-service, but got: " + e.toString());
            return null;
        }
    }

}
