package de.kieseltaucher.studies.tracing.rest;

import java.util.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import de.kieseltaucher.studies.tracing.outbound.TimeOfDayClient;
import de.kieseltaucher.studies.tracing.domain.TimeOfDay;
import de.kieseltaucher.studies.tracing.tracing.TraceLogger;

@Path("/hello")
public class HelloWorldEndpoint {

	private final Logger logger = Logger.getLogger(getClass().getName());

	@Inject
	private TimeOfDayClient timeOfDayClient;

	@Inject
	private TraceLogger traceLogger;

	@GET
	@Produces("text/plain")
	public Response doGet() {
		logger.info("GET /hello");
		TimeOfDay timeOfDay = getTimeOfDay();
		String response = chooseResponse(timeOfDay);
		return Response.ok(response).build();
	}

	private TimeOfDay getTimeOfDay() {
		traceLogger.log("Calling time-of-day-service");
		TimeOfDay timeOfDay = timeOfDayClient.getTimeOfDay();
		traceLogger.log(String.format("The time of day according to time-of-day-service is %s", timeOfDay));
		return timeOfDay;
	}

	private String chooseResponse(TimeOfDay timeOfDay) {
		if (timeOfDay == null) {
			return "Hi";
		}
		switch (timeOfDay) {
			case MORNING:
				return "Good morning";
			case AFTERNOON:
				return "Good afternoon";
			case EVENING:
				return "Good night";
			default:
				throw new IllegalStateException("My goodness, it's the middle of the night");
		}

	}
}
