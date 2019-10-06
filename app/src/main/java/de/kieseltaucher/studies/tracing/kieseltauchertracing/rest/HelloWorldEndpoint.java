package de.kieseltaucher.studies.tracing.kieseltauchertracing.rest;


import java.util.logging.Logger;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;


@Path("/hello")
public class HelloWorldEndpoint {

	private final Logger logger = Logger.getLogger(getClass().getName());

	@GET
	@Produces("text/plain")
	public Response doGet() {
		logger.info("/hello");
		return Response.ok("Hello from Thorntail!").build();
	}
}
