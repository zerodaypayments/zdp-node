package io.zdp.node.service.validation.getAccounts;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.zdp.crypto.Base58;
import io.zdp.model.network.NetworkTopologyService;
import io.zdp.node.service.validation.cache.UnconfirmedTransferMemoryPool;
import io.zdp.node.service.validation.consensus.TransferConsensusService;
import io.zdp.node.service.validation.model.UnconfirmedTransfer;
import io.zdp.node.service.validation.service.ValidationNodeSigner;

/**
 * 
 * @author sn_1970@yahoo.com
 *
 */
@Component(value = "getNodeAccountsResponseQueueListener")
public class GetNodeAccountsResponseQueueListener {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private NetworkTopologyService networkTopologyService;

	@Autowired
	private UnconfirmedTransferMemoryPool unconfirmedTransferMemoryPool;

	@Autowired
	private ValidationNodeSigner validationNodeSigner;

	@Autowired
	private TransferConsensusService transferConsensusService;
	
	public void onMessage(final GetNodeAccountsResponse resp) {

		log.debug("Got accounts response from: " + resp.getServerUuid());

		if (validationNodeSigner.isValidSignature(resp)) {

			final UnconfirmedTransfer unconfirmedTransfer = unconfirmedTransferMemoryPool.get(resp.getTransferUuid());

			if (unconfirmedTransfer != null) {

				synchronized (unconfirmedTransfer) {

					if (false == unconfirmedTransfer.isReadyToSettle()) {

						log.debug("Got unconfirmed transfer: " + unconfirmedTransfer);

						unconfirmedTransfer.getConfirmations().add(resp);

						final int confirmationCount = unconfirmedTransfer.getConfirmations().size();

						// enough to settle?
						log.debug("Got confirmations: " + confirmationCount);

						if (confirmationCount >= networkTopologyService.getAllNodes().size()) {

							log.debug("Ready to settle");

							unconfirmedTransfer.setReadyToSettle(true);

							unconfirmedTransferMemoryPool.remove(unconfirmedTransfer);

							transferConsensusService.process(unconfirmedTransfer);
						}

					} else {
						log.debug("Already settled, ignoring response: " + resp);
					}

				}

			} else {
				log.warn("Unconfirmed transfer doesn't exist: " + Base58.encode(resp.getTransferUuid()));
			}

		} else {

			log.error("Message not validated: " + resp);

		}

	}

}
