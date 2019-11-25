package de.kieseltaucher.studies.tracing.inbound;

import java.util.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import de.kieseltaucher.studies.tracing.app.TimeOfDay;
import de.kieseltaucher.studies.tracing.app.TimeOfDayService;

@Path("/time-of-day")
public class TimeOfDayResource {

    private final Logger logger = Logger.getLogger(getClass().getName());

    @Inject
    private TimeOfDayService timeOfDayService;
    @GET
    @Produces("application/json")
    public TimeOfDay doGet() {
        logger.info("GET /time-of-day");
        return timeOfDayService.getTimeOfDay();
    }

}
