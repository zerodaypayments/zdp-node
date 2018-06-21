package io.zdp.node.service.validation.failed;

import javax.jms.ConnectionFactory;
import javax.jms.Topic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;

/**
 * @author sn_1970@yahoo.com
 */
public class FailedTransferRequestTopicPublisher {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	private JmsTemplate jmsTemplate;

	private Topic topic;

	public void send(final FailedTransferRequest req) {

		log.debug("FailedTransferRequestTopicPublisher: " + req);

		jmsTemplate.convertAndSend(topic, req);

	}

	public void setConnectionFactory(ConnectionFactory connectionFactory) {
		this.jmsTemplate = new JmsTemplate(connectionFactory);
	}

	public void setTopic(Topic topic) {
		this.topic = topic;
	}

}
