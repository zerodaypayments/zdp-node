package io.zdp.node.service.validation.getAccounts;

import javax.jms.ConnectionFactory;
import javax.jms.Topic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;

/**
 * @author sn_1970@yahoo.com
 */
public class GetNodeAccountsRequestTopicPublisher {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	private JmsTemplate jmsTemplate;

	private Topic topic;

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
