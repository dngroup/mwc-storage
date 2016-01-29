/**
 * 
 */
package org.cnrs.jdev;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

/**
 * @author dbourasseau
 *
 */
public interface RequestService {

	abstract void pushTranscodeMessage(String string, URI uri) throws IOException;

	abstract String getMetadata(String ID) throws IOException;

	abstract InputStream getContent(URI uri) throws IOException;

}
