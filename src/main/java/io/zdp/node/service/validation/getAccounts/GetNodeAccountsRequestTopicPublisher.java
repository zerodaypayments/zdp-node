package io.zdp.node.service.validation.getAccounts;

import java.util.UUID;

import javax.jms.ConnectionFactory;
import javax.jms.Topic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * @author sn_1970@yahoo.com
 */
public class GetNodeAccountsRequestTopicPublisher {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	private JmsTemplate jmsTemplate;

	private Topic topic;

	//@Scheduled(fixedDelay = 6000)
	public void gen() {

		GetNodeAccountsRequest req = new GetNodeAccountsRequest();
		byte[] arr = new byte[] { 1, 2, 3, 4 };
		req.setFromAccountUuid(arr);
		req.setServerSignature(arr);
		req.setServerUuid(UUID.randomUUID().toString());
		req.setToAccountUuid(arr);
		req.setTransactionUuid(arr);
		
		log.debug("Published: " + req);

		send(req);
	}

	public void send(final GetNodeAccountsRequest req) {

		log.debug("GetNodeAccountsSender: " + req);

		jmsTemplate.convertAndSend(topic, req);

	}

	public void setConnectionFactory(ConnectionFactory connectionFactory) {
		this.jmsTemplate = new JmsTemplate(connectionFactory);
	}

	public void setTopic(Topic topic) {
		this.topic = topic;
	}

}
