package io.zdp.node.network.validation;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.config.JmsListenerConfigUtils;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import io.zdp.model.network.NetworkNode;
import io.zdp.model.network.NetworkTopologyListener;
import io.zdp.model.network.NetworkTopologyService;
import io.zdp.node.service.LocalNodeService;
import io.zdp.node.service.validation.getAccounts.GetNodeAccountsRequest;
import io.zdp.node.service.validation.getAccounts.GetNodeAccountsResponse;

@Service
public class ValidationNetworkMQ implements NetworkTopologyListener {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private NetworkTopologyService networkTopologyService;

	@Autowired
	private LocalNodeService nodeConfigurationService;

	private Map<String, JmsTemplate> jmsTemplates = new HashMap<>();

	@PostConstruct
	public void init() {
		onChange();
		this.networkTopologyService.addChangeListener(this);
	}

	@Override
	public void onChange() {
		log.debug("Network topology changed, rebuild ");
	}

	/**
	 * Send confirmed transfer back to the originating server
	 */
	public void send(final String serverUuid, final GetNodeAccountsResponse c) {

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
		
		GetNodeAccountsRequest req = (GetNodeAccountsRequest) template.receiveAndConvert(MQNames.TOPIC_TRANSFER_NEW_REQ);

//		jmsTemplates.get(serverUuid).convertAndSend(Queues.Q_TRANSFER_NEW_RESP, c);

	}

}
