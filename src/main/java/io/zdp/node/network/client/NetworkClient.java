package io.zdp.node.network.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.zdp.api.model.v1.TransferRequest;
import io.zdp.node.network.topology.NetworkNodeService;

/**
 * Ask Validation network nodes on their opinion on the Transfer Request.
 * 
 * Go ahead with the majority of the nodes.  
 *
 */
@Service
public class NetworkClient {

	@Autowired
	private NetworkNodeService networkNodeService;

	private Logger log = LoggerFactory.getLogger(this.getClass());

	public void prepare(TransferRequest req, String accountUuid, String toUuid) {

		log.debug("Prepare " + req);
		
 		if (networkNodeService.getNodes().isEmpty()) {
			
			log.debug("I am the only validation node, I am very agreeable");
			
		} else {
			
			networkNodeService.getNodes().parallelStream().forEach(e -> {
				log.debug("Validation node: " + e.getUuid());
			});
			
		}


	}

}