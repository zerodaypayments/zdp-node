package io.zdp.node.service.validation.balance.get;

import java.util.List;

import org.apache.logging.log4j.ThreadContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.zdp.crypto.Base58;
import io.zdp.model.network.NetworkTopologyService;
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

	@Autowired
	private NetworkTopologyService networkTopologyService;

	@Autowired
	private BalanceRequestResolver resolver;

	public void onMessage(final BalanceResponse resp) {

		log.debug("Got accounts balance response: " + resp.getServerUuid());

		final String uuid = Base58.encode(resp.getAccountUuid());

		ThreadContext.put(NewTransfersService.TRANSFER_UUID, "BALANCE " + uuid);

		if (validationNodeSigner.isValidSignature(resp)) {

			BalanceRequest balanceRequest = balanceRequestCache.get(resp.getAccountUuid());

			final List<BalanceResponse> balances = balanceRequest.getResponses();

			balances.add(resp);

			synchronized (balanceRequest) {

				log.debug("Responses: " + balances.size() + " for " + uuid);

				if (balances.size() >= networkTopologyService.getAllNodes().size()) {
					resolver.resolve(balanceRequest);
				}
			}

		} else {

			log.error("Message not validated: " + resp);

		}

	}

}
