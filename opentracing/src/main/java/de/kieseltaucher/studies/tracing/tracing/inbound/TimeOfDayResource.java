package de.kieseltaucher.studies.tracing.tracing.inbound;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.util.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import de.kieseltaucher.studies.tracing.tracing.app.TimeOfDay;
import de.kieseltaucher.studies.tracing.tracing.app.TimeOfDayService;

@Path("/time-of-day")
public class TimeOfDayResource {

    private final Logger logger = Logger.getLogger(getClass().getName());

    @Inject
    private TimeOfDayService timeOfDayService;
    @GET
    @Produces(APPLICATION_JSON)
    public TimeOfDay doGet() {
        logger.info("GET /time-of-day");
        return timeOfDayService.getTimeOfDay();
    }

}
