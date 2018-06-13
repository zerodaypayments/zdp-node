package io.zdp.node.service.validation.getAccounts;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.zdp.crypto.Signing;
import io.zdp.node.network.validation.NetworkMQ;
import io.zdp.node.service.LocalNodeService;

/**
 * Transfer confirmation listener for Validation nodes
 * 
 * @author sn_1970@yahoo.com
 *
 */
@Component(value = "getNodeAccountsListener")
public class GetNodeAccountsListener {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private LocalNodeService localNodeService;

	@Autowired
	private GetNodeAccountsService getNodeAccountsService;

	@Autowired
	private NetworkMQ networkMQ;

	/**
	 * MQ Listener
	 */
	public void onMessage(final GetNodeAccountsRequest t) {

		final GetNodeAccountsResponse resp = getNodeAccountsService.process(t);

		resp.setServerUuid(localNodeService.getNode().getUuid());

		try {
			resp.setServerSignature(Signing.sign(localNodeService.getNode().getECPrivateKey(), resp.toHash()));
		} catch (Exception e) {
			log.error("Error: ", e);
		}

		networkMQ.send(t.getServerUuid(), resp);
	}

}
