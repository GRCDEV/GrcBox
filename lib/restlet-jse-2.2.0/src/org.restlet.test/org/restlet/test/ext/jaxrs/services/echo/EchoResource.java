package org.restlet.test.ext.jaxrs.services.echo;

import java.awt.Point;

import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path(value = EchoResource.path)
public interface EchoResource {
	public static final String path = "echo";

	@GET
	@Path("point-header")
	@Consumes(MediaType.APPLICATION_JSON)
	Point echoPointHeaderParam(@HeaderParam("point") Point point);

	@GET
	@Path("point-query")
	@Consumes(MediaType.APPLICATION_JSON)
	Point echoPointQueryParam(/** using @Deprecated to test the annotation mapping logic*/
	@Deprecated @QueryParam("point") Point point);

	@POST
	String echo(String input);

	@GET
	@Path("point-path/{point}")
	@Consumes(MediaType.APPLICATION_JSON)
	Point echoPointPathParam(@PathParam("point") Point point);

	@GET
	@Path("point-path/{input:[Tt]his_Is_A_Test\\d+}")
	@Consumes(MediaType.APPLICATION_JSON)
	String echoStringRegexPathParam(@PathParam("input") String input);

	@GET
	@Path("point-cookie")
	@Consumes(MediaType.APPLICATION_JSON)
	Point echoPointCookieParam(@CookieParam("point")Point point);
}
