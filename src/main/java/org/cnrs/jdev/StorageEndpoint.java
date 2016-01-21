package org.cnrs.jdev;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.media.multipart.FormDataParam;


/**
 * Root resource (exposed at "myresource" path)
 */
@Path("storage")
public class StorageEndpoint {

	DataStoreService dataStoreService = new DataStoreServiceNotification(
			new DataStoreServiceImpl());
	RequestService requestService = new RequestServiceImp();
	private static final String FS_ROOT = "/var/www/dummy/";
	
    @HEAD
    @Path("{contentId}/{resolution}")
    @Produces("video/mp4")
    public Response header(final @PathParam("contentId") String contentId,
			final @PathParam("resolution") String resolution) {
    	 final File asset = new File(FS_ROOT
    				+ contentId + "/" + resolution);
        return Response.ok().status(206).header(HttpHeaders.CONTENT_LENGTH, asset.length()).build();
    }

	
	@GET
	@Produces("video/mp4")
	@Path("{contentId}/{resolution}")
	public Response getIt(@HeaderParam("Range") String range ,final @PathParam("contentId") String contentId,
			final @PathParam("resolution") String resolution) throws FileNotFoundException, IOException {
			return dataStoreService.stream(contentId, resolution,range);
	}

	@POST
	@Path("{contentId}/{resolution}")
	public Response postId(@PathParam("contentId") String contentId,
			@PathParam("resolution") String resolution, InputStream is)
			throws IOException {

		dataStoreService.put(contentId, resolution, is);
		return Response.ok().build();

	}
	
	@Path("metadata/{contentId}")
	@GET
	public Response getmetadata(@PathParam("contentId") String contentId)throws IOException {
		
		String response = requestService.getMetadata(contentId);
		
		return Response.ok(response).type(MediaType.APPLICATION_JSON).build();

	}
	public static String storageHostname = System.getenv().get("STORAGE_HOSTNAME");
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response postOriginal(@FormDataParam("file") InputStream is)
			throws IOException {
		
		UUID uuid = UUID.randomUUID();
		URI uriContent = UriBuilder.fromUri("http://"+storageHostname).port(CliConfSingleton.myPort).path("api").path("storage").path(uuid.toString()+".mp4").build();
//		URI urimetadata = UriBuilder.fromUri("http://172.17.42.1").port(CliConfSingleton.myPort).path("api").path("storage").path("metadata").path(uuid.toString()+".mp4").build();
		
		dataStoreService.put(uuid.toString()+".mp4", is);
		requestService.pushTranscodeMessage(uuid.toString(),uriContent);
		String s =URI.create("storage/metadata/"+uuid.toString()).toString();
		return Response.ok(s).location(URI.create("storage/"+uuid.toString())).build();

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
				}, "video/mp4")
				.header("content-disposition",
						"attachment; filename = original.mp4")
				.build();
	}
}
