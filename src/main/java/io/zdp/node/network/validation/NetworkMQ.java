package io.zdp.node.network.validation;

import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import io.zdp.model.network.NetworkNode;
import io.zdp.model.network.NetworkTopologyListener;
import io.zdp.model.network.NetworkTopologyService;
import io.zdp.node.service.LocalNodeService;
import io.zdp.node.service.validation.getAccounts.GetNodeAccountsRequest;
import io.zdp.node.service.validation.getAccounts.GetNodeAccountsResponse;

@Service
public class NetworkMQ implements NetworkTopologyListener {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private NetworkTopologyService networkTopologyService;

	@Autowired
	private LocalNodeService nodeConfigurationService;

	static class NodeMQ {

		private String serverUuid;

		private String url;

		public String getServerUuid() {
			return serverUuid;
		}

		public void setServerUuid(String serverUuid) {
			this.serverUuid = serverUuid;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((serverUuid == null) ? 0 : serverUuid.hashCode());
			result = prime * result + ((url == null) ? 0 : url.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			NodeMQ other = (NodeMQ) obj;
			if (serverUuid == null) {
				if (other.serverUuid != null)
					return false;
			} else if (!serverUuid.equals(other.serverUuid))
				return false;
			if (url == null) {
				if (other.url != null)
					return false;
			} else if (!url.equals(other.url))
				return false;
			return true;
		}

	}

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

		//S		template.convertAndSend(TransferConfirmationGateway.QUEUE_TRANSFER_CONFIRMATION, c);

	}

	public void broadcastToValidationNetwork(GetNodeAccountsRequest transferConfirmationRequest) {
		// TODO Auto-generated method stub

	}

}
