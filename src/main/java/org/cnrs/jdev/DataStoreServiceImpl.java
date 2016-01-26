package org.cnrs.jdev;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Date;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

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
			File  f = new File(FS_ROOT+ contentid + "/");
			f.mkdirs();
			File  f2 = new File(FS_ROOT+ contentid + "/" + resolution);
			FileOutputStream fos = new FileOutputStream(f2);
			ByteStreams.copy(is, fos);
			
			fos.flush();
			fos.close();
			LOGGER.debug("putting {}/{} done", contentid, resolution);
		} catch (IOException ie) {
			ie.printStackTrace();

		}

	}
	
	final int chunk_size = Integer.MAX_VALUE/2; // 1MB chunks
	
	@Override
	public Response stream(String contentid, String resolution, String range) throws IOException {
		   final File asset = new File(FS_ROOT
   				+ contentid + "/" + resolution);
		  if (range == null) {
		  StreamingOutput streamer = new StreamingOutput() {
	            @Override
	            public void write(final OutputStream output) throws IOException, WebApplicationException {

	             
					final FileChannel inputChannel = new FileInputStream(asset).getChannel();
	                final WritableByteChannel outputChannel = Channels.newChannel(output);
	                try {
	                    inputChannel.transferTo(0, inputChannel.size(), outputChannel);
	                } finally {
	                    // closing the channels
	                    inputChannel.close();
	                    outputChannel.close();
	                }
	            }
	        };
	        return Response.ok(streamer).header(HttpHeaders.CONTENT_LENGTH, asset.length()).build();
		  }

		    String[] ranges = range.split("=")[1].split("-");
		    final int from = Integer.parseInt(ranges[0]);
		    /**
		     * Chunk media if the range upper bound is unspecified. Chrome sends "bytes=0-"
		     */
//		    int to = chunk_size + from;
//		    if (to >= asset.length()) {
		    int   to = (int) (asset.length() - 1);
//		    }
		    if (ranges.length == 2) {
		        to = Integer.parseInt(ranges[1]);
		    }

		    final String responseRange = String.format("bytes %d-%d/%d", from, to, asset.length());
		    final RandomAccessFile raf = new RandomAccessFile(asset, "r");
		    raf.seek(from);

		    final int len = to - from + 1;
		    final MediaStreamer streamer = new MediaStreamer(len, raf);
		    Response.ResponseBuilder res = Response.ok(streamer).status(206)
		            .header("Accept-Ranges", "bytes")
		            .header("Content-Range", responseRange)
		            .header(HttpHeaders.CONTENT_LENGTH, streamer.getLenth())
		            .header(HttpHeaders.LAST_MODIFIED, new Date(asset.lastModified()));
            return res.build();

	}

	public void get(String contentid, String resolution, OutputStream os)
			throws IOException {
		
		final FileChannel inputChannel = new FileInputStream(new File(FS_ROOT
				+ contentid + "/" + resolution)).getChannel();
        final WritableByteChannel outputChannel = Channels.newChannel(os);
        try {
            inputChannel.transferTo(0, inputChannel.size(), outputChannel);
        } finally {
            // closing the channels
            inputChannel.close();
            outputChannel.close();
        }
		
		
		
//		try {
//			
//			
//			LOGGER.info("getting {}/{}", contentid, resolution);
//			FileInputStream fis = new FileInputStream(new File(FS_ROOT
//					+ contentid + "/" + resolution));
//			 ByteStreams.copy(fis, os);
//			
//			fis.close();
//			LOGGER.debug("getting {}/{} done");
//		} catch (IOException ie) {
//			ie.printStackTrace();
//
//		}

	}

	@Override
	public void put(String contentid, InputStream is) throws IOException {
		try {
			
			File  f = new File(FS_ROOT);
			f.mkdirs();
			File  f2 = new File(FS_ROOT+ contentid);
			
			
			LOGGER.info("putting {}/{}", contentid);
			FileOutputStream fos = new FileOutputStream(f2);
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
