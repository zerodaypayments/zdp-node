package io.zdp.node.service.validation.failed;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.zdp.node.service.validation.service.ValidationNodeSigner;

/**
 * Failed transfers listener for Validation nodes
 * 
 * @author sn_1970@yahoo.com
 *
 */
@Component
public class FailedTransferRequestTopicListener implements MessageListener {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private ValidationNodeSigner validationNodeSigner;

	@Autowired
	private FailedTransferService failedTransferService;

	@Override
	public void onMessage(Message message) {

		try {

			final ObjectMessage om = (ObjectMessage) message;

			final FailedTransferRequest req = (FailedTransferRequest) om.getObject();

			if (validationNodeSigner.isValidSignature(req)) {

				log.debug("FailedTransferRequestTopicListener: " + req);

				failedTransferService.fail(req);

			} else {
				log.error("Can't verify request: " + req);
			}

		} catch (JMSException e) {
			log.error("Error: ", e);
		}
	}

}
