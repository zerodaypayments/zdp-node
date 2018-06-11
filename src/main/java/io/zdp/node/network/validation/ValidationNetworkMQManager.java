package io.zdp.node.network.validation;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import io.zdp.model.network.NetworkNode;
import io.zdp.model.network.NetworkTopologyService;
import io.zdp.node.service.NodeConfigurationService;
import io.zdp.node.service.validation.listener.TransferConfirmationGateway;
import io.zdp.node.service.validation.model.TransferConfirmationResponse;

@Service
public class ValidationNetworkMQManager {

	private final Logger log=LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private NetworkTopologyService networkTopologyService;

	@Autowired
	private NodeConfigurationService nodeConfigurationService;

	/**
	 * Send confirmed transfer back to the originating server
	 */
	public void send(final String serverUuid, final TransferConfirmationResponse c) {
		
		log.debug("Send [" + c + "] to [" + serverUuid + "]");
		
		final NetworkNode originatingNode = networkTopologyService.getNodeByUuid(serverUuid);
		
		final String brokerURL = "tcp://" + originatingNode.getAmqHostname() + ":" + originatingNode.getAmqPort();
		log.debug("brokerURL: " + brokerURL);
		
		// Cache AMQ objects
		org.apache.activemq.ActiveMQConnectionFactory amqcf = new org.apache.activemq.ActiveMQConnectionFactory();
		amqcf.setBrokerURL(brokerURL);

		org.apache.activemq.pool.PooledConnectionFactory pcf = new org.apache.activemq.pool.PooledConnectionFactory();
		pcf.setConnectionFactory(amqcf);
		pcf.setCreateConnectionOnStartup(true);
		pcf.setMaxConnections(1024);
		pcf.setTimeBetweenExpirationCheckMillis(DateUtils.MILLIS_PER_SECOND);
		pcf.setUseAnonymousProducers(true);
		
		JmsTemplate template = new JmsTemplate(pcf);
		template.setDeliveryPersistent(false);

		template.convertAndSend(TransferConfirmationGateway.QUEUE_TRANSFER_CONFIRMATION, c);
		
	}

}
