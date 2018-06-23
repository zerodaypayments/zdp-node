package io.zdp.node.service.validation.balance;

import org.apache.logging.log4j.ThreadContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.zdp.node.service.validation.service.NewTransfersService;
import io.zdp.node.service.validation.service.ValidationNodeSigner;

/**
 * 
 * @author sn_1970@yahoo.com
 *
 */
@Component(value = "getBalanceResponseQueueListener")
public class BalanceResponseQueueListener {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private ValidationNodeSigner validationNodeSigner;

	@Autowired
	private BalanceRequestCache balanceRequestCache;

	public void onMessage(final BalanceResponse resp) {

		log.debug("Got accounts balance response: " + resp.getServerUuid());

		ThreadContext.put(NewTransfersService.TRANSFER_UUID, "BALANCE " + resp.getAccount().getUuid());

		if (validationNodeSigner.isValidSignature(resp)) {

			BalanceRequest balanceRequest = balanceRequestCache.get(resp.getAccountUuid());

			balanceRequest.getResponses().add(resp);

		} else {

			log.error("Message not validated: " + resp);

		}

	}

}
