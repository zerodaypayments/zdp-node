package io.zdp.node.network.server;

import java.util.UUID;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import io.zdp.api.model.v1.PingResponse;
import io.zdp.api.model.v1.SystemErrorResponse;

@Component(value = "networkServer")
public class NetworkServer {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Value("${local.node.tcp.server.host}")
	private String tcpServerHost;

	@Value("${local.node.tcp.server.port}")
	private String tcpServerPort;

	private ObjectMapper jsonMapper;

	@PostConstruct
	public void init() {
		log.debug("Starting TCP server: " + tcpServerHost + ":" + tcpServerPort);

		jsonMapper = new ObjectMapper();
		jsonMapper.enable(SerializationFeature.INDENT_OUTPUT);
	}

	public String process(String message) throws JsonProcessingException {

		log.debug("Process: " + message);

		try {
			
			if (message.startsWith("ping")) {
				return jsonMapper.writeValueAsString(new PingResponse());
			}

			return UUID.randomUUID().toString();
			
		} catch (JsonProcessingException e) {
			log.error("Error: ", e);
		}

		return jsonMapper.writeValueAsString(new SystemErrorResponse());
	}

}
