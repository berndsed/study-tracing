package de.kieseltaucher.studies.tracing.kieseltauchertracing.rest;

import java.util.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import de.kieseltaucher.studies.tracing.kieseltauchertracing.tracing.TraceLogger;

@Path("/hello")
public class HelloWorldEndpoint {

	private final Logger logger = Logger.getLogger(getClass().getName());

	@Inject
	private TraceLogger traceLogger;

	@GET
	@Produces("text/plain")
	public Response doGet() {
		logger.info("/hello");
		traceLogger.log("A log message from the hello-service");
		return Response.ok("Hello from Thorntail!").build();
	}
}
