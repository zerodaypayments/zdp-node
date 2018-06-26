package io.zdp.node.service.validation.balance.update;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.logging.log4j.ThreadContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.zdp.crypto.Base58;
import io.zdp.node.service.validation.service.NewTransfersService;
import io.zdp.node.service.validation.service.ValidationNodeSigner;

/**
 * @author sn_1970@yahoo.com
 */
@Component
public class UpdateBalanceRequestTopicListener implements MessageListener {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private ValidationNodeSigner validationNodeSigner;

	@Autowired
	private UpdateBalanceService service;

	@Override
	public void onMessage(Message message) {

		try {

			final ObjectMessage om = (ObjectMessage) message;

			final UpdateBalanceRequest req = (UpdateBalanceRequest) om.getObject();

			ThreadContext.put(NewTransfersService.TRANSFER_UUID, "ACCOUNT BALANCE UPDATE " + Base58.encode(req.getAccountUuid()));

			if (validationNodeSigner.isValidSignature(req)) {

				log.debug("UpdateBalanceRequestTopicListener: " + req);

				service.update(req);

			} else {
				log.error("Can't verify request: " + req);
			}

		} catch (JMSException e) {
			log.error("Error: ", e);
		}
	}

}
