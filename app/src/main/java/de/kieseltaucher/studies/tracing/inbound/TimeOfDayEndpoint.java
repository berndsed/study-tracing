package de.kieseltaucher.studies.tracing.inbound;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.util.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.ServiceUnavailableException;

import de.kieseltaucher.studies.tracing.domain.TimeOfDay;
import de.kieseltaucher.studies.tracing.domain.TimeOfDayService;

@Path("/time-of-day")
public class TimeOfDayEndpoint {

    private final Logger logger = Logger.getLogger(getClass().getName());

    @Inject
    private TimeOfDayService timeOfDayService;
    @Inject
    private Availability availability;

    @GET
    @Produces(APPLICATION_JSON)
    public TimeOfDay doGet() {
        logger.info("GET /time-of-day");
        throwIfUnavailable();
        return timeOfDayService.getTimeOfDay();
    }

    private void throwIfUnavailable() {
        if (!availability.isAvailable()) {
            logger.warning("Time-of-day-service is unavailable");
            throw new ServiceUnavailableException("The time-of-day-service is currently unavailable. Please come back later.");
        }
    }

}
