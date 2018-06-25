package io.zdp.node.service.validation.balance;

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
import io.zdp.node.service.validation.mq.ValidationNetworkMQ;
import io.zdp.node.service.validation.service.NewTransfersService;
import io.zdp.node.service.validation.service.ValidationNodeSigner;
import io.zdp.node.storage.account.domain.Account;
import io.zdp.node.storage.account.service.AccountService;

/**
 * Transfer confirmation listener for Validation nodes
 * 
 * @author sn_1970@yahoo.com
 *
 */
@Component
public class BalanceRequestTopicListener implements MessageListener {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private AccountService accountService;

	@Autowired
	private ValidationNetworkMQ networkMQ;

	@Autowired
	private ValidationNodeSigner validationNodeSigner;

	@Override
	public void onMessage(Message message) {

		try {

			final ObjectMessage om = (ObjectMessage) message;

			final BalanceRequest req = (BalanceRequest) om.getObject();

			ThreadContext.put(NewTransfersService.TRANSFER_UUID, "BALANCE " + Base58.encode(req.getAccountUuid()));

			if (validationNodeSigner.isValidSignature(req)) {

				log.debug("BalanceRequestTopicListener: " + req);

				Account acc = this.accountService.findByUuid(req.getAccountUuid());

				if (acc != null) {
					networkMQ.send(req.getServerUuid(), new BalanceResponse(acc));
				} else {
					log.warn("No account found, sending empty response back: " + req.toString());
					networkMQ.send(req.getServerUuid(), new BalanceResponse(req.getAccountUuid()));
				}

			} else {
				log.error("Can't verify request: " + req);
			}

		} catch (JMSException e) {
			log.error("Error: ", e);
		}
	}

}
