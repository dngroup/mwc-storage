package org.cnrs.jdev;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CORSResponseFilter implements ContainerResponseFilter {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(CORSResponseFilter.class);

	public void filter(ContainerRequestContext requestContext,
			ContainerResponseContext responseContext) throws IOException {

		MultivaluedMap<String, Object> headers = responseContext.getHeaders();

		headers.add("Access-Control-Allow-Origin", "*");
		// headers.add("Access-Control-Allow-Origin",
		// "http://podcastpedia.org"); //allows CORS requests only coming from
		// podcastpedia.org
		// headers.add("Access-Control-Allow-Methods",
		// "GET, POST, DELETE, PUT");
		// headers.add("Access-Control-Allow-Headers",
		// "X-Requested-With, Content-Type, X-Codingpedia");
		LOGGER.info(requestContext.getUriInfo().getAbsolutePath().toString());
	}

}