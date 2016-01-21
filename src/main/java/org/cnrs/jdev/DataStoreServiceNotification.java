package org.cnrs.jdev;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.research.ws.wadl.Application;

public class DataStoreServiceNotification implements DataStoreService {

	private static final Client client = ClientBuilder.newClient();
	private static final Logger LOGGER = LoggerFactory
			.getLogger(DataStoreServiceNotification.class);
	private DataStoreService target;

	public DataStoreServiceNotification(DataStoreService target) {
		this.target = target;
	}

	@Override
	public void put(String contentid, String resolution, InputStream is)
			throws IOException {
		target.put(contentid, resolution, is);
//		Response res = client
//				.target(CliConfSingleton.getVanillaURI())
//				.path("content")
//				.path(contentid)
//				.path(resolution)
//				.request()
//				.put(Entity.entity(
//						"{'url':'" + CliConfSingleton.getBaseURI()
//								+ "storage/" + contentid + "/" + resolution
//								+ "'}",
//						MediaType.APPLICATION_JSON));
//
//		if (res.getStatus() >= 400) {
//			LOGGER.error("failed to notify middle server {} {} ",
//					res.getStatus());
//		}

	}

	@Override
	public void get(String contentid, String resolution, OutputStream os)
			throws IOException {
		target.get(contentid, resolution, os);

	}

	@Override
	public void put(String contentid, InputStream is) throws IOException {
		target.put(contentid, is);
		
	}

	@Override
	public void get(String contentid, OutputStream os) throws IOException {
		target.get(contentid, os);
		
	}

	@Override
	public Response stream(String contentid, String resolution, String range) throws FileNotFoundException, IOException {
		return target.stream(contentid, resolution,range);
	}



}
