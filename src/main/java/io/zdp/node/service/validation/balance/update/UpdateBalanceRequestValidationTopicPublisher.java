package io.zdp.node.service.validation.balance.update;

import javax.jms.ConnectionFactory;
import javax.jms.Topic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;

/**
 * @author sn_1970@yahoo.com
 */
public class UpdateBalanceRequestValidationTopicPublisher {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	private JmsTemplate jmsTemplate;

	private Topic topic;

	public void send(final UpdateBalanceRequest req) {

		log.debug("UpdateBalanceRequestTopicPublisher: " + req);

		jmsTemplate.convertAndSend(topic, req);

	}

	public void setConnectionFactory(ConnectionFactory connectionFactory) {
		this.jmsTemplate = new JmsTemplate(connectionFactory);
	}

	public void setTopic(Topic topic) {
		this.topic = topic;
	}

}
