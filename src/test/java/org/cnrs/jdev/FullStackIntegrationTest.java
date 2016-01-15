package org.cnrs.jdev;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.w3c.dom.Document;

import com.google.common.hash.Hashing;
import com.google.common.io.ByteStreams;

@Category(IntegrationTest.class)
public class FullStackIntegrationTest {

	@Test
	public void longRunningServiceTest() throws Exception {

		Client client = ClientBuilder.newClient();

		// push file to server
		URL originalURL = getClass().getResource("/marseille.3gp");
		URL mediumURL = getClass().getResource("/medium.mp4");
		Path originalPath = Paths.get(originalURL.toURI());
		Path mediumPath = Paths.get(mediumURL.toURI());

		String originalSha1 = com.google.common.io.Files.hash(
				originalPath.toFile(), Hashing.sha1()).toString();

		String mediumSha1 = com.google.common.io.Files.hash(
				mediumPath.toFile(), Hashing.sha1()).toString();

		FileInputStream fis = new FileInputStream(originalPath.toFile());

		Response res = client.target("http://localhost:8081/api/content")
				.request()
				.post(Entity.entity(fis, MediaType.APPLICATION_OCTET_STREAM));

		fis.close();
		Assert.assertEquals(201, res.getStatus());
		URI location = UriBuilder.fromUri(res.getHeaderString("Location"))
				.build();

		Thread.sleep(2000);
		// get original file
		{
			InputStream is = client.target(location).request()
					.accept(MediaType.APPLICATION_OCTET_STREAM)
					.get(InputStream.class);

			Path tempFile = Files.createTempFile(null, null);
			FileOutputStream fos = new FileOutputStream(tempFile.toFile());
			ByteStreams.copy(is, fos);
			fos.flush();
			fos.close();
			Assert.assertEquals(
					originalSha1,
					com.google.common.io.Files.hash(tempFile.toFile(),
							Hashing.sha1()).toString());
		}

		// wait 20 s for the file to be transcoded
		Thread.sleep(20000);
		String metadata = client
				.target(UriBuilder.fromUri(location).path("metadata"))
				.request().accept(MediaType.APPLICATION_XML).get(String.class);

		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(new ByteArrayInputStream(metadata.getBytes()));
		XPathFactory xPathfactory = XPathFactory.newInstance();
		XPath xpath = xPathfactory.newXPath();
		XPathExpression expr = xpath.compile("//resolution[@name='medium']/@uri");
		String mediumUriStr = expr.evaluate(doc);
		URI mediumURI =  UriBuilder.fromUri(mediumUriStr).build();
		
		Assert.assertEquals(8082, mediumURI.getPort());
	

		{
			InputStream is = client
					.target(mediumURI)
					.request().accept(MediaType.APPLICATION_OCTET_STREAM)
					.get(InputStream.class);

			Path tempFile = Files.createTempFile(null, null);
			FileOutputStream fos = new FileOutputStream(tempFile.toFile());
			ByteStreams.copy(is, fos);
			fos.flush();
			fos.close();
			Assert.assertEquals(
					mediumSha1,
					com.google.common.io.Files.hash(tempFile.toFile(),
							Hashing.sha1()).toString());
		}

	}
}
