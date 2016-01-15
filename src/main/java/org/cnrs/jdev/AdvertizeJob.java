package org.cnrs.jdev;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class AdvertizeJob implements Job {

	protected static final Client client = ClientBuilder.newClient();

	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
//		Entity<String> temp = Entity.entity(
//				"{\"thirdPartyStorage\":{\"id\":5,\"name\":\"dummy\",\"url\":\"http:\\/\\/"
//						+ CliConfSingleton.myHostName + ":"
//						+ CliConfSingleton.myPort
//						+ "\\/api\\/storage\"}}",
//				MediaType.APPLICATION_JSON);
		client.target(
				"http://" + CliConfSingleton.vanillaHostName + ":"
						+ CliConfSingleton.vanillaPort + "/api/thirdpartystorage")
				.request()
				.post(Entity.entity(
						"{\"thirdPartyStorage\":{\"id\":5,\"name\":\"dummy\",\"url\":\"http:\\/\\/"
								+ CliConfSingleton.myHostName + ":"
								+ CliConfSingleton.myPort
								+ "\\/api\\/storage\"}}",
						MediaType.APPLICATION_JSON));

	}
}