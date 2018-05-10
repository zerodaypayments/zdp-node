package io.zdp.node.network.service;

import java.io.File;
import java.net.URL;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.zdp.node.network.domain.NetworkNode;

@Component
public class NetworkConfigurationService {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	private List<NetworkNode> nodes;

	@Scheduled(initialDelay = 3 * DateUtils.MILLIS_PER_SECOND, fixedDelay = 5 * DateUtils.MILLIS_PER_SECOND)
	public synchronized void init() {

		log.debug("Refreshing network configuration");

		try {
			// Download public VNL (List of Validation Nodes) file
			final URL url = new File("vnl.json").toURI().toURL();

			final ObjectMapper jsonMapper = new ObjectMapper();
			jsonMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

			final List<NetworkNode> topology = jsonMapper.readValue(url, new TypeReference<List<NetworkNode>>() {
			});

			if (nodes == null || false == CollectionUtils.isEqualCollection(topology, nodes)) {

				nodes = topology;

				log.debug("Loaded/reloaded list of validation nodes: " + nodes);

			} else {
				log.debug("No change in network topology");
			}

		} catch (Exception e) {
			log.error("Error: ", e);
		}

	}

	public List<NetworkNode> getNodes() {

		if (nodes == null) {
			init();
		}

		return nodes;

	}

}
