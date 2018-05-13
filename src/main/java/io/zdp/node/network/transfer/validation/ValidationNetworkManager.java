package io.zdp.node.network.transfer.validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.zdp.api.model.v1.TransferRequest;
import io.zdp.node.network.topology.NetworkNodeService;

@Service
public class ValidationNetworkManager {

	@Autowired
	private NetworkNodeService networkNodeService;

	private Logger log = LoggerFactory.getLogger(this.getClass());

	public void prepare(TransferRequest req) {

		log.debug("Prepare " + req);

	}

}