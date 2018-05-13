package io.zdp.node.http;

import javax.annotation.PostConstruct;

import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.handler.*;
import org.eclipse.jetty.webapp.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

//@Service
public class HttpServer {

	private Logger log = LoggerFactory.getLogger(this.getClass());

//	@PostConstruct
	public void init() throws Exception {

		Server server = new Server(8080);

		HandlerCollection handlers = new HandlerCollection();

		WebAppContext webapp1 = new WebAppContext();
		webapp1.setResourceBase("/webapp/");
		webapp1.setContextPath("/api");
		webapp1.setDefaultsDescriptor("/webapp/WEB-INF/web.xml");
		handlers.addHandler(webapp1);

		// Adding the handlers to the server
		server.setHandler(handlers);

		// Starting the Server
		server.start();
		System.out.println("Started!");
		server.join();

	}

}
