package org.cnrs.jdev;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.URI;
import java.net.URL;
import java.util.UUID;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.glassfish.grizzly.utils.Charsets;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;

import fr.labri.progress.comet.conf.CliConfSingleton;

/**
 * Root resource (exposed at "myresource" path)
 */
@Path("storage")
public class StorageEndpoint {

	DataStoreService dataStoreService = new DataStoreServiceNotification(
			new DataStoreServiceImpl());
	RequestService requestService = new RequestServiceImp();

	@GET
	@Path("{contentId}/{resolution}")
	public Response getIt(final @PathParam("contentId") String contentId,
			final @PathParam("resolution") String resolution) {
		return Response
				.ok(new StreamingOutput() {

					@Override
					public void write(OutputStream output) throws IOException,
							WebApplicationException {
						dataStoreService.get(contentId, resolution, output);

					}
				}, MediaType.APPLICATION_OCTET_STREAM)
				.header("content-disposition",
						"attachment; filename = " + resolution + ".mp4")
				.build();
	}

	@POST
	@Path("{contentId}/{resolution}")
	public Response postId(@PathParam("contentId") String contentId,
			@PathParam("resolution") String resolution, InputStream is)
			throws IOException {

		dataStoreService.put(contentId, resolution, is);
		return Response.ok().build();

	}
	
	
	
	@POST
	public Response postOriginal(InputStream is)
			throws IOException {
		
		UUID uuid = UUID.randomUUID();
		URI uriContent = UriBuilder.fromUri("http://172.17.42.1").port(CliConfSingleton.myPort).path("api").path("storage").path(uuid.toString()).build();
		
		dataStoreService.put(uuid.toString(), is);
		requestService.pushTranscodeMessage(uuid.toString(),uriContent);
		return Response.ok().location(URI.create("storage/"+uuid.toString())).build();

	}
	
	@GET
	@Path("{contentId}")
	public Response getOriginal(@PathParam("contentId") String contentId) {
		return Response
				.ok(new StreamingOutput() {

					@Override
					public void write(OutputStream output) throws IOException,
							WebApplicationException {
						
//						try {
							dataStoreService.get(contentId, output);
//						} catch (IOException e) {
//							System.out.println("File not found try with hash");
//							String addr = Inet4Address.getLocalHost().getHostAddress();
//							URI uri = UriBuilder.fromUri(addr).path("storage").path(contentId).build();
//							HashCode hash = Hashing.sha1().hashString(uri.toASCIIString(),
//									Charsets.ASCII_CHARSET);
//							dataStoreService.get(hash.toString(), output);
//							
//						}

					}
				}, MediaType.APPLICATION_OCTET_STREAM)
				.header("content-disposition",
						"attachment; filename = original.mp4")
				.build();
	}
}
