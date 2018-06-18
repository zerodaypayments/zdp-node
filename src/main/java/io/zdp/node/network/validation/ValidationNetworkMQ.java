package io.zdp.node.network.validation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.stereotype.Service;

import io.zdp.model.network.NetworkNode;
import io.zdp.model.network.NetworkTopologyListener;
import io.zdp.model.network.NetworkTopologyService;
import io.zdp.node.service.LocalNodeService;
import io.zdp.node.service.validation.getAccounts.GetNodeAccountsRequestTopicListener;
import io.zdp.node.service.validation.getAccounts.GetNodeAccountsResponse;

@Service
public class ValidationNetworkMQ implements NetworkTopologyListener {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private NetworkTopologyService networkTopologyService;

	@Autowired
	private LocalNodeService nodeConfigurationService;

	@Autowired
	private GetNodeAccountsRequestTopicListener getNodeAccountsRequestTopicListener;

	@Autowired
	@Qualifier("default-task-executor")
	private TaskExecutor taskExecutor;

	private Map<NetworkNode, List<DefaultMessageListenerContainer>> listeners = new HashMap<>();

	@PostConstruct
	public void init() {
		onChange();
		this.networkTopologyService.addChangeListener(this);
	}

	@Override
	public void onChange() {

		log.debug("Network topology changed, rebuild ");

		final List<NetworkNode> nodes = this.networkTopologyService.getAllBut(this.nodeConfigurationService.getNode());

		for (NetworkNode remoteNode : nodes) {

			listeners.put(remoteNode, new ArrayList<DefaultMessageListenerContainer>());

			final String brokerURL = "tcp://" + remoteNode.getAmqHostname() + ":" + remoteNode.getAmqPort();
			log.debug("remote brokerURL: " + brokerURL);

			// Cache AMQ objects
			ActiveMQConnectionFactory amqcf = new ActiveMQConnectionFactory();
			amqcf.setBrokerURL(brokerURL);

			org.apache.activemq.pool.PooledConnectionFactory pcf = new org.apache.activemq.pool.PooledConnectionFactory();
			pcf.setConnectionFactory(amqcf);
			pcf.setCreateConnectionOnStartup(true);
			pcf.setMaxConnections(1024);
			pcf.setUseAnonymousProducers(true);

			// Remote GetAccounts request
			{
				DefaultMessageListenerContainer l = createListener(pcf, MQNames.TOPIC_TRANSFER_NEW_REQ);
				listeners.get(remoteNode).add(l);
			}

			{
				DefaultMessageListenerContainer l = createListener(pcf, MQNames.TOPIC_SETTLED_TRANSFER_REQ);
				listeners.get(remoteNode).add(l);
			}

			{
				DefaultMessageListenerContainer l = createListener(pcf, MQNames.TOPIC_CANCELLED_TRANSFER_REQ);
				listeners.get(remoteNode).add(l);
			}

		}

	}

	private DefaultMessageListenerContainer createListener(ConnectionFactory pcf, String name) {
		DefaultMessageListenerContainer l = new DefaultMessageListenerContainer();
		l.setConnectionFactory(pcf);
		l.setDestinationName(name);
		l.setTaskExecutor(taskExecutor);
		l.setMessageListener(getNodeAccountsRequestTopicListener);
		l.afterPropertiesSet();
		l.start();
		return l;
	}

	/**
	 * Send confirmed transfer back to the originating server
	 */
	public void send(final String serverUuid, final GetNodeAccountsResponse c) {

		log.debug("Send [" + c + "] to [" + serverUuid + "]");

		final NetworkNode originatingNode = networkTopologyService.getNodeByUuid(serverUuid);

		// Remote receive settle transfers

	}

}
