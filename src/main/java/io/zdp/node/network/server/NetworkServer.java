package io.zdp.node.network.server;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component(value = "networkServer")
public class NetworkServer {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	public String process(String message) {

		log.debug("Process: " + message);

		return  UUID.randomUUID().toString();
	}

}
