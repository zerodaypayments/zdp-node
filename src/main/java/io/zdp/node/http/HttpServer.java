package io.zdp.node.http;

import java.io.File;
import java.lang.management.ManagementFactory;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;
import org.eclipse.jetty.jmx.MBeanContainer;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.LowResourceMonitor;
import org.eclipse.jetty.server.NCSARequestLog;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnectionStatistics;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.RequestLogHandler;
import org.eclipse.jetty.server.handler.StatisticsHandler;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ScheduledExecutorScheduler;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.zdp.node.Node;

//@Service
public class HttpServer {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	private static int MAX_THREADS = 512;

	//	@PostConstruct
	public void init() throws Exception {

		QueuedThreadPool threadPool = new QueuedThreadPool();
		threadPool.setMaxThreads(MAX_THREADS);

		// Server
		Server server = new Server(threadPool);

		// Scheduler
		server.addBean(new ScheduledExecutorScheduler());

		// HTTP Configuration
		HttpConfiguration http_config = new HttpConfiguration();

		http_config.setOutputBufferSize(32768);
		http_config.setRequestHeaderSize(8192);
		http_config.setResponseHeaderSize(8192);
		http_config.setSendServerVersion(true);
		http_config.setSendDateHeader(false);

		// Handler Structure
		HandlerCollection handlers = new HandlerCollection();
		ContextHandlerCollection contexts = new ContextHandlerCollection();

		WebAppContext webapp1 = new WebAppContext();
		webapp1.setResourceBase("/webapp/");
		webapp1.setContextPath("/");
		webapp1.setDefaultsDescriptor("/webapp/WEB-INF/web.xml");

		handlers.setHandlers(new Handler[] { contexts, webapp1 });

		// Adding the handlers to the server

		server.setHandler(handlers);

		// Extra options
		server.setDumpAfterStart(false);
		server.setDumpBeforeStop(false);
		server.setStopAtShutdown(true);

		// === jetty-jmx.xml ===
		MBeanContainer mbContainer = new MBeanContainer(ManagementFactory.getPlatformMBeanServer());
		server.addBean(mbContainer);

		// === jetty-http.xml ===
		ServerConnector http = new ServerConnector(server, new HttpConnectionFactory(http_config));
		http.setPort(Node.getLocalNode().getHttpPort());
		http.setIdleTimeout(30000);
		server.addConnector(http);
		
		log.debug("HTTP port: " + http.getPort());

		// SSL HTTP Configuration
		HttpConfiguration https_config = new HttpConfiguration(http_config);
		https_config.addCustomizer(new SecureRequestCustomizer());

		// === jetty-stats.xml ===
		StatisticsHandler stats = new StatisticsHandler();
		stats.setHandler(server.getHandler());
		server.setHandler(stats);

		ServerConnectionStatistics.addToAllConnectors(server);

		// === jetty-requestlog.xml ===
		NCSARequestLog requestLog = new NCSARequestLog();

		File requestFile = new File(SystemUtils.getUserHome(), ".zdp" + File.separator + "logs" + File.separator + "http" + File.separator + "http.log");
		FileUtils.forceMkdirParent(requestFile);

		requestLog.setFilename(requestFile.getAbsolutePath());
		requestLog.setRetainDays(90);
		requestLog.setAppend(true);
		requestLog.setExtended(true);
		requestLog.setLogCookies(false);
		requestLog.setLogTimeZone("GMT");
		RequestLogHandler requestLogHandler = new RequestLogHandler();
		requestLogHandler.setRequestLog(requestLog);
		handlers.addHandler(requestLogHandler);

		// === jetty-lowresources.xml ===
		LowResourceMonitor lowResourcesMonitor = new LowResourceMonitor(server);
		lowResourcesMonitor.setPeriod(1000);
		lowResourcesMonitor.setLowResourcesIdleTimeout(200);
		lowResourcesMonitor.setMonitorThreads(true);
		lowResourcesMonitor.setMaxConnections(0);
		lowResourcesMonitor.setMaxMemory(0);
		lowResourcesMonitor.setMaxLowResourcesTime(5000);
		server.addBean(lowResourcesMonitor);

		// Start the server
		server.start();
		server.join();

	}

}
