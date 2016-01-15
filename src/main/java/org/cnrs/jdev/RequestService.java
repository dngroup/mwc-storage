/**
 * 
 */
package org.cnrs.jdev;

import java.io.IOException;
import java.net.URI;

/**
 * @author dbourasseau
 *
 */
public interface RequestService {

	abstract void pushTranscodeMessage(String string, URI uri) throws IOException;

}
