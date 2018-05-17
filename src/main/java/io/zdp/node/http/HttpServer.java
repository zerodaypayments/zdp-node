package io.zdp.node.http;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;
import org.eclipse.jetty.http.HttpVersion;
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
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.RequestLogHandler;
import org.eclipse.jetty.server.handler.StatisticsHandler;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ScheduledExecutorScheduler;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//@Service
public class HttpServer {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	private static String PROTOCOL = "https";
	private static int HTTPS_PORT = 8443;
	
	private static int MAX_THREADS = 512;

	//	@PostConstruct
	public void init() throws Exception {
		
        // === jetty.xml ===
        // Setup Threadpool
        QueuedThreadPool threadPool = new QueuedThreadPool();
        threadPool.setMaxThreads(MAX_THREADS);

        // Server
        Server server = new Server(threadPool);

        // Scheduler
        server.addBean(new ScheduledExecutorScheduler());

        // HTTP Configuration
        HttpConfiguration http_config = new HttpConfiguration();
        http_config.setSecureScheme("https");
        http_config.setSecurePort(HTTPS_PORT);
        http_config.setOutputBufferSize(32768);
        http_config.setRequestHeaderSize(8192);
        http_config.setResponseHeaderSize(8192);
        http_config.setSendServerVersion(true);
        http_config.setSendDateHeader(false);
        // httpConfig.addCustomizer(new ForwardedRequestCustomizer());

        // Handler Structure
        HandlerCollection handlers = new HandlerCollection();
        ContextHandlerCollection contexts = new ContextHandlerCollection();

		WebAppContext webapp1 = new WebAppContext();
		webapp1.setResourceBase("/webapp/");
		webapp1.setContextPath("/zdp");
		webapp1.setDefaultsDescriptor("/webapp/WEB-INF/web.xml");

		//handlers.addHandler(webapp1);
		
        handlers.setHandlers(new Handler[] { contexts, webapp1 });


		// Adding the handlers to the server
        
        server.setHandler(handlers);

        // Extra options
        server.setDumpAfterStart(false);
        server.setDumpBeforeStop(false);
        server.setStopAtShutdown(true);

        // === jetty-jmx.xml ===
        MBeanContainer mbContainer = new MBeanContainer(
                ManagementFactory.getPlatformMBeanServer());
        server.addBean(mbContainer);


        // === jetty-http.xml ===
        /*
        ServerConnector http = new ServerConnector(server,
                new HttpConnectionFactory(http_config));
        http.setPort(8080);
        http.setIdleTimeout(30000);
        server.addConnector(http);
        */


        // === jetty-https.xml ===
        // SSL Context Factory
        SslContextFactory sslContextFactory = new SslContextFactory();
        /*
        sslContextFactory.setKeyStorePath(jetty_home + "/../../../jetty-server/src/test/config/etc/keystore");
        sslContextFactory.setKeyStorePassword("OBF:1vny1zlo1x8e1vnw1vn61x8g1zlu1vn4");
        sslContextFactory.setKeyManagerPassword("OBF:1u2u1wml1z7s1z7a1wnl1u2g");
        sslContextFactory.setTrustStorePath(jetty_home + "/../../../jetty-server/src/test/config/etc/keystore");
        sslContextFactory.setTrustStorePassword("OBF:1vny1zlo1x8e1vnw1vn61x8g1zlu1vn4");
         */
        
        String pass = "some password";
        
        KeyPair kp = StoreKeyPair.generateKeyPair();
		Certificate cert = StoreKeyPair.selfSign(kp, "CN=localhost");
		
		File keyStoreFile = new File(SystemUtils.getJavaIoTmpDir(), UUID.randomUUID().toString());
		
		StoreKeyPair.storeToPKCS12(keyStoreFile.getAbsolutePath(), pass.toCharArray(), kp);
		
		log.debug("Saved keystore: " + keyStoreFile.getAbsolutePath());
		
        sslContextFactory.setKeyStorePath(keyStoreFile.getAbsolutePath());
        sslContextFactory.setKeyStorePassword(pass);
        sslContextFactory.setKeyManagerPassword(pass);
        sslContextFactory.setTrustStorePath(keyStoreFile.getAbsolutePath());
        sslContextFactory.setTrustStorePassword(pass);
        
        
//		ks.setKeyEntry("localhost", kp.getPrivate(), pass, new Certificate[]{cert});
        

        // SSL HTTP Configuration
        HttpConfiguration https_config = new HttpConfiguration(http_config);
        https_config.addCustomizer(new SecureRequestCustomizer());

        // SSL Connector
		ServerConnector sslConnector = new ServerConnector(server, new SslConnectionFactory(sslContextFactory, HttpVersion.HTTP_1_1.asString()), new HttpConnectionFactory(https_config));
		sslConnector.setPort(HTTPS_PORT);
		server.addConnector(sslConnector);


        // === jetty-stats.xml ===
        StatisticsHandler stats = new StatisticsHandler();
        stats.setHandler(server.getHandler());
        server.setHandler(stats);
        ServerConnectionStatistics.addToAllConnectors(server);

        // === jetty-requestlog.xml ===
        NCSARequestLog requestLog = new NCSARequestLog();
		
        File requestFile = new File ( SystemUtils.getUserHome(), ".zdp" + File.separator + "logs" + File.separator + "http" + File.separator + "yyyy_mm_dd.request.log");
        FileUtils.forceMkdirParent(requestFile);
        
		
		requestLog.setFilename(requestFile.getAbsolutePath());
        requestLog.setFilenameDateFormat("yyyy_MM_dd");
        requestLog.setRetainDays(90);
        requestLog.setAppend(true);
        requestLog.setExtended(true);
        requestLog.setLogCookies(false);
        requestLog.setLogTimeZone("GMT");
        RequestLogHandler requestLogHandler = new RequestLogHandler();
        requestLogHandler.setRequestLog(requestLog);
        handlers.addHandler(requestLogHandler);

        // === jetty-lowresources.xml ===
        LowResourceMonitor lowResourcesMonitor=new LowResourceMonitor(server);
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
		
		
		/*
		QueuedThreadPool threadPool = new QueuedThreadPool();
		threadPool.setMaxThreads(MAX_THREADS);

		Server server = new Server(threadPool);
		
        server.addBean(new ScheduledExecutorScheduler());

        // HTTP Configuration
        HttpConfiguration http_config = new HttpConfiguration();
        http_config.setSecureScheme(PROTOCOL);
        http_config.setSecurePort(HTTPS_PORT);
        http_config.setOutputBufferSize(32768);
        http_config.setRequestHeaderSize(8192);
        http_config.setResponseHeaderSize(8192);
        http_config.setSendServerVersion(true);
        http_config.setSendDateHeader(true);
        
        // Handler Structure
        HandlerCollection handlers = new HandlerCollection();
        ContextHandlerCollection contexts = new ContextHandlerCollection();
        handlers.setHandlers(new Handler[] { contexts, new DefaultHandler() });
        server.setHandler(handlers);

        // Extra options
        server.setDumpAfterStart(false);
        server.setDumpBeforeStop(false);
        server.setStopAtShutdown(true);
        */
        
        
		
		/*
		HttpConfiguration http_config = new HttpConfiguration();
		http_config.setSecureScheme(PROTOCOL);
		http_config.setSecurePort(PORT);

		HttpConfiguration https_config = new HttpConfiguration(http_config);
		https_config.addCustomizer(new SecureRequestCustomizer());

		SslContextFactory sslContextFactory = new SslContextFactory(true);
		sslContextFactory.setKeyStorePassword("password");


		ServerConnector httpsConnector = new ServerConnector(server, new SslConnectionFactory(sslContextFactory, "http/1.1"), new HttpConnectionFactory(https_config));
		httpsConnector.setPort(PORT);
		httpsConnector.setIdleTimeout(50000);

		server.setConnectors(new Connector[] { httpsConnector });

		log.debug("Starting HTTP server on "+PORT+" with context path /zdp");

		HandlerCollection handlers = new HandlerCollection();
        
		WebAppContext webapp1 = new WebAppContext();
		webapp1.setResourceBase("/webapp/");
		webapp1.setContextPath("/zdp");
		webapp1.setDefaultsDescriptor("/webapp/WEB-INF/web.xml");
		handlers.addHandler(webapp1);

		// Adding the handlers to the server
		server.setHandler(handlers);

		// Starting the Server
		server.start();
		server.join();
		 */

	}

}
