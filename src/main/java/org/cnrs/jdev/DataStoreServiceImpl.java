package org.cnrs.jdev;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import com.google.common.io.ByteStreams;

public class DataStoreServiceImpl implements DataStoreService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(DataStoreServiceImpl.class);

	private static final String FS_ROOT = "/var/www/dummy/";

	public void put(String contentid, String resolution, InputStream is)
			throws IOException {
		try {
			LOGGER.info("putting {}/{}", contentid, resolution);
			FileOutputStream fos = new FileOutputStream(new File(FS_ROOT
					+ contentid + "_" + resolution));
			ByteStreams.copy(is, fos);
			
			fos.flush();
			fos.close();
			LOGGER.debug("putting {}/{} done", contentid, resolution);
		} catch (IOException ie) {
			ie.printStackTrace();

		}

	}

	public void get(String contentid, String resolution, OutputStream os)
			throws IOException {
		try {
			LOGGER.info("getting {}/{}", contentid, resolution);
			FileInputStream fis = new FileInputStream(new File(FS_ROOT
					+ contentid + "_" + resolution));
			 ByteStreams.copy(fis, os);
			
			fis.close();
			LOGGER.debug("getting {}/{} done");
		} catch (IOException ie) {
			ie.printStackTrace();

		}

	}

	@Override
	public void put(String contentid, InputStream is) throws IOException {
		try {
			LOGGER.info("putting {}/{}", contentid);
			FileOutputStream fos = new FileOutputStream(new File(FS_ROOT
					+ contentid));
			ByteStreams.copy(is, fos);
			
			fos.flush();
			fos.close();
			LOGGER.debug("putting {} done", contentid);
		} catch (IOException ie) {
			ie.printStackTrace();

		}
		
	}

	@Override
	public void get(String contentid, OutputStream os) throws IOException {
		try {
			LOGGER.info("getting {}", contentid);
			FileInputStream fis = new FileInputStream(new File(FS_ROOT
					+ contentid ));
			 ByteStreams.copy(fis, os);
			
			fis.close();
			LOGGER.debug("getting {} done", contentid);
		} catch (IOException ie) {
			ie.printStackTrace();

		}
		
	}

}
