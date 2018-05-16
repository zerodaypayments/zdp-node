package io.zdp.node.http;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//@Service
public class HttpServer {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	//	@PostConstruct
	public void init() throws Exception {

		Server server = new Server(8080);
		
		log.debug("Starting HTTP server on 8080 with context path /zdp");

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

	}

}
