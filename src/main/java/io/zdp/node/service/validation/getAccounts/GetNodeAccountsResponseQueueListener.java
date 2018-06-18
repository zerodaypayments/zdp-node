package io.zdp.node.service.validation.getAccounts;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.zdp.model.network.NetworkTopologyService;
import io.zdp.node.network.validation.ValidationNetworkMQ;
import io.zdp.node.service.LocalNodeService;
import io.zdp.node.service.validation.UnconfirmedTransferMemoryPool;
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
	private LocalNodeService localNodeService;

	@Autowired
	private GetNodeAccountsService getNodeAccountsService;

	@Autowired
	private ValidationNetworkMQ networkMQ;

	@Autowired
	private NetworkTopologyService networkTopologyService;

	@Autowired
	private UnconfirmedTransferMemoryPool unconfirmedTransferMemoryPool;

	@Autowired
	private ValidationNodeSigner validationNodeSigner;

	public void onMessage(final GetNodeAccountsResponse resp) {

		log.debug("Got accounts response from: " + resp.getServerUuid());

		if (validationNodeSigner.isValidSignature(resp)) {

			final UnconfirmedTransfer unconfirmedTransfer = unconfirmedTransferMemoryPool.get(resp.getTransferUuid());
			
			
			

		} else {

			log.error("Message not validated: " + resp);

		}

	}

}
