package de.kieseltaucher.studies.tracing.tracing.inbound;

import java.util.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import de.kieseltaucher.studies.tracing.tracing.app.GreetService;
import de.kieseltaucher.studies.tracing.tracing.app.ServiceUnavailableException;

@Path("/hello")
public class GreetResource {

	private final Logger logger = Logger.getLogger(getClass().getName());

	@Inject
	private GreetService greetService;

	@GET
	@Produces("text/plain")
	public Response doGet() {
		logger.info("GET /hello");
		final String response;
		try {
			response = greetService.getGreeting();
		} catch (ServiceUnavailableException e) {
			throw new javax.ws.rs.ServiceUnavailableException(e.getMessage());
		}
		return Response.ok(response).build();
	}

}
