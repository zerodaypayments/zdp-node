package io.zdp.node.service.validation.getAccounts;

import java.util.concurrent.ThreadLocalRandom;

import javax.jms.ConnectionFactory;
import javax.jms.Topic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Scheduled;

import io.zdp.node.service.LocalNodeService;

/**
 * @author sn_1970@yahoo.com
 */
public class GetNodeAccountsRequestTopicPublisher {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	private JmsTemplate jmsTemplate;

	private Topic topic;

	private LocalNodeService localNodeService;

//	@Scheduled(fixedDelay = 2000)
	public void gen() {

		GetNodeAccountsRequest req = new GetNodeAccountsRequest();

		byte[] arr = new byte[32];

		ThreadLocalRandom.current().nextBytes(arr);

		req.setFromAccountUuid(arr);
		req.setServerUuid(localNodeService.getNode().getUuid());
		req.setToAccountUuid(arr);
		req.setTransactionUuid(arr);

		log.debug("Published: " + req);

		send(req);
	}

	public void send(final GetNodeAccountsRequest req) {

		log.debug("GetNodeAccountsSender: " + req);

		jmsTemplate.convertAndSend(topic, req);

	}

	public void setLocalNodeService(LocalNodeService localNodeService) {
		this.localNodeService = localNodeService;
	}

	public void setConnectionFactory(ConnectionFactory connectionFactory) {
		this.jmsTemplate = new JmsTemplate(connectionFactory);
	}

	public void setTopic(Topic topic) {
		this.topic = topic;
	}

}
