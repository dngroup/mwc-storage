package org.cnrs.jdev;

import java.io.IOException;
import java.net.URI;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;




public class RequestServiceImp implements RequestService {
	private static final Logger LOGGER = LoggerFactory.getLogger(RequestServiceImp.class);

	Client client = ClientBuilder.newClient();

	@Override
	public void pushTranscodeMessage(String ID, URI uri) throws IOException {
		Content content = new Content();
		content.setId(ID);
		content.setUri(uri.toString());
		
		URI frontendURI = UriBuilder.fromUri(CliConfSingleton.getVanillaURI()).path("api").path("content").build();
		WebTarget target = client.target(frontendURI);
		
		 Response response = null;
		try {
			response = target.request(MediaType.APPLICATION_XML_TYPE)
					.post(Entity.entity(content, MediaType.APPLICATION_XML));
			switch (Status.fromStatusCode(response.getStatus())) {
			case ACCEPTED:
				// normal statement but don't is normally not that
				break;
			case CREATED:
				// normal statement
				break;
			case OK:
				// normal statement but don't use this because normally we need
				// return a object
				break;
			case CONFLICT:
				// throw new SuchUserException();
			default:
				throw new IOException(
						"Can not conect to the server : POST on this link"
								+ target.getUri() + +response.getStatus());
			}
		} catch (ProcessingException e) {
			LOGGER.error("Can not connect to the remote host {} ",frontendURI,e);
			throw new WebApplicationException("Can not connect to the remote host",502) ;
		}

		
		
	}

}
