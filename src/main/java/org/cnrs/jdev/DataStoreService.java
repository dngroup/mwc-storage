package org.cnrs.jdev;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.ws.rs.core.Response;

public interface DataStoreService {

	public void put(String contentid, String resolution, InputStream is)
			throws IOException;

	public void get(String contentid, String resolution, OutputStream os)
			throws IOException;

	public void put(String contentid, InputStream is)
			throws IOException;

	public void get(String contentid, OutputStream os)
			throws IOException;

	public Response stream(String contentid, String resolution, String range) throws FileNotFoundException, IOException;
	

}