package org.cnrs.jdev;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.Properties;

import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.StaticHttpHandler;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

import com.lexicalscope.jewel.cli.CliFactory;
import com.lexicalscope.jewel.cli.HelpRequestedException;
import com.lexicalscope.jewel.cli.Option;


/**
 * Main class.
 *
 */
public class Main {

	// Base URI the Grizzly HTTP server will listen on

	/**
	 * Starts Grizzly HTTP server exposing JAX-RS resources defined in this
	 * application.
	 * 
	 * @return Grizzly HTTP server.
	 */
	public static HttpServer startServer() {
		// create a resource config that scans for JAX-RS resources and
		// providers
		// in org.cnrs.jdev package
		final ResourceConfig rc = new ResourceConfig()
				.packages("org.cnrs.jdev").register(CORSResponseFilter.class).register(MultiPartFeature.class);
		
		// create and start a new instance of grizzly http server
		// exposing the Jersey application at BASE_URI
		return GrizzlyHttpServerFactory.createHttpServer(
				URI.create(CliConfSingleton.getBaseURI()), rc);
	}

	/**
	 * Main method.
	 * 
	 * @param args
	 * @throws IOException
	 * @throws SchedulerException
	 */
	public static void main(String[] args) throws IOException,
			SchedulerException {

		try {
			CliConfiguration cliconf = CliFactory.parseArguments(
					CliConfiguration.class, args);

			CliConfSingleton.myHostName = cliconf.getMyHostName();
			CliConfSingleton.myPort = cliconf.getMyPort();
			CliConfSingleton.vanillaHostName = cliconf.getVanillaHostName();
			CliConfSingleton.vanillaPort = cliconf.getVanillaPort();

			final HttpServer server = startServer();

			HttpHandler videos = new StaticHttpHandler("/var/www/dummy");
			server.getServerConfiguration().addHttpHandler(videos,"/video");
//TODO: uncomment this line
//			setupCron();

			System.out.println(String.format(
					"Jersey app started with WADL available at "
							+ "%sapplication.wadl\nHit enter to stop it...",
					CliConfSingleton.getBaseURI()));
			Thread.currentThread().join();
		} catch (HelpRequestedException | InterruptedException ios) {
			System.out.println(ios.getMessage());
		}
	}

	private static void setupCron() throws SchedulerException {
		StdSchedulerFactory sf = new StdSchedulerFactory();
		Properties props = new Properties();
		props.put("org.quartz.threadPool.threadCount", "1");
		sf.initialize(props);

		Scheduler sched = sf.getScheduler();
		{// resource from frontal
			JobDetail job = newJob(AdvertizeJob.class).withIdentity(
					"advertizeStorage", "dummy").build();
			Trigger trigger = newTrigger()
					.withSchedule(
							simpleSchedule().withIntervalInSeconds(10)
									.repeatForever()).startAt(new Date())
					.build();

			sched.scheduleJob(job, trigger);

		}
		sched.start();

	}
}

class CliConfSingleton {
	public static String myHostName;
	public static String vanillaHostName;
	public static int myPort;
	public static int vanillaPort;

	public static String getBaseURI() {
		return "http://" + myHostName + ":" + myPort + "/api/";
	}

	public static String getVanillaURI() {
		return "http://" + vanillaHostName + ":" + vanillaPort + "/api/";
	}
}

interface CliConfiguration {

	@Option(longName = "myHostName", defaultToNull = false)
	String getMyHostName();

	@Option(longName = "myPort", defaultToNull = false)
	int getMyPort();

	@Option(longName = "vanillaStorageHostName", defaultToNull = false)
	String getVanillaHostName();

	@Option(longName = "vanillaStoragePort", defaultToNull = false)
	int getVanillaPort();
}
