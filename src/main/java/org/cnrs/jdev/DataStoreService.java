package org.cnrs.jdev;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface DataStoreService {

	public void put(String contentid, String resolution, InputStream is)
			throws IOException;

	public void get(String contentid, String resolution, OutputStream os)
			throws IOException;

	public void put(String contentid, InputStream is)
			throws IOException;

	public void get(String contentid, OutputStream os)
			throws IOException;

}