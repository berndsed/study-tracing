package de.kieseltaucher.studies.tracing.outbound;

import java.util.logging.Logger;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.ws.rs.ServiceUnavailableException;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.MediaType;

import de.kieseltaucher.studies.tracing.app.TimeOfDay;

@Dependent
public class TimeOfDayClientImpl implements TimeOfDayClient {

    private final Logger logger = Logger.getLogger(getClass().getName());

    @Inject
    private Client client;

    @Override
    public TimeOfDay getTimeOfDay() {
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
