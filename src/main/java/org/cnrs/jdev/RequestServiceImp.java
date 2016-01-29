package org.cnrs.jdev;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
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
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestServiceImp implements RequestService {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(RequestServiceImp.class);

	Client client = ClientBuilder.newClient();

	@Override
	public void pushTranscodeMessage(String ID, URI uri) throws IOException {
		Content content = new Content();
		content.setId(ID);
		content.setUri(uri.toString());

		StringWriter writer = new StringWriter();
		try {
			JAXBContext.newInstance(Content.class).createMarshaller()
					.marshal(content, writer);
		} catch (JAXBException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String frontendHostname= System.getenv().get("FRONTEND_HOSTNAME");
		if (frontendHostname==null){
			frontendHostname="frontend";
		}
		URI frontendURI = UriBuilder.fromUri("http://"+frontendHostname)
				.port(CliConfSingleton.vanillaPort).path("api").path("content")
				.build();
		WebTarget target = client.target(frontendURI);
		LOGGER.info("POST to", frontendURI);
		Response response = null;
		try {
			response = target.request(MediaType.APPLICATION_XML_TYPE).post(
					Entity.entity(writer.toString(),
							MediaType.APPLICATION_XML_TYPE));
			switch (Status.fromStatusCode(response.getStatus())) {
			case OK:
				// normal statement but don't use this because normally we need
				// return a object
				break;
			default:
				throw new IOException(
						"Can not conect to the server : POST on this link"
								+ target.getUri() + +response.getStatus());
			}
		} catch (ProcessingException e) {
			LOGGER.error("Can not connect to the remote host {} ", frontendURI,
					e);
			throw new WebApplicationException(
					"Can not connect to the remote host", 502);
		}

	}

	@Override
	public String getMetadata(String ID) throws IOException {
		String frontendHostname= System.getenv().get("FRONTEND_HOSTNAME");
		if (frontendHostname==null){
			frontendHostname="frontend";
		}
		URI frontendURI = UriBuilder.fromUri("http://"+frontendHostname)
				.port(CliConfSingleton.vanillaPort).path("api").path("content")
				.path(ID).build();
		WebTarget target = client.target(frontendURI);
		LOGGER.info("Get Metadata to", frontendURI);
		Response response = null;
		try {
			response = target.request().get();
			return response.readEntity(String.class);

		} catch (ProcessingException e) {
			LOGGER.error("Can not connect to the remote host {} ", frontendURI,
					e);
			throw new WebApplicationException(
					"Can not connect to the remote host", 502);
		}
	}
	
	@Override
	public InputStream getContent(URI uri) throws IOException {

	
		WebTarget target = client.target(uri);
		
		Response response = null;
		try {
			response = target.request().get();
			return response.readEntity(InputStream.class);

		} catch (ProcessingException e) {
			LOGGER.error("Can not connect to the remote host {} ", uri,
					e);
			throw new WebApplicationException(
					"Can not connect to the remote host", 502);
		}
	}
}
