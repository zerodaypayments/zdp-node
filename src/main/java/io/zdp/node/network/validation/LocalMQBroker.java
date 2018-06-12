package io.zdp.node.network.validation;

import javax.annotation.PostConstruct;

import org.apache.activemq.broker.BrokerService;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.zdp.model.network.NetworkTopologyService;
import io.zdp.node.Node;
import io.zdp.node.service.NodeConfigurationService;
import io.zdp.node.service.validation.model.TransferConfirmationRequest;

@Service
public class LocalMQBroker {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private NetworkTopologyService networkTopologyService;

	@Autowired
	private NodeConfigurationService nodeConfigurationService;

	@PostConstruct
	public void init() throws Exception {

		String amqHost = System.getProperty(Node.PARAM_AMQ_VALIDATION_HOST);
		String amqPort = System.getProperty(Node.PARAM_AMQ_VALIDATION_PORT);

		String bindUrl = "tcp://" + amqHost + ":" + amqPort;

		log.debug("Started ValidationNetworkNodeMQListener at [" + bindUrl + "]");

		BrokerService brokerService = new BrokerService();
		brokerService.setPersistent(false);
		brokerService.setUseJmx(false);
		brokerService.addConnector(bindUrl);
		brokerService.start();

	}

	public void send(TransferConfirmationRequest transferConfirmationRequest) {

	}

}
