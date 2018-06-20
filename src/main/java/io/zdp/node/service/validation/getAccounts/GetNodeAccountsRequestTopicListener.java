package io.zdp.node.service.validation.getAccounts;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.zdp.crypto.Signing;
import io.zdp.node.network.validation.ValidationNetworkMQ;
import io.zdp.node.service.LocalNodeService;
import io.zdp.node.service.validation.service.ValidationNodeSigner;

/**
 * Transfer confirmation listener for Validation nodes
 * 
 * @author sn_1970@yahoo.com
 *
 */
@Component(value = "getNodeAccountsRequestTopicListener")
public class GetNodeAccountsRequestTopicListener implements MessageListener {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private LocalNodeService localNodeService;

	@Autowired
	private GetNodeAccountsService getNodeAccountsService;

	@Autowired
	private ValidationNetworkMQ networkMQ;

	@Autowired
	private ValidationNodeSigner validationNodeSigner;

	@Override
	public void onMessage(Message message) {

		try {

			final ObjectMessage om = (ObjectMessage) message;

			final GetNodeAccountsRequest req = (GetNodeAccountsRequest) om.getObject();

			if (validationNodeSigner.isValidSignature(req)) {

				log.debug("getNodeAccountsRequestTopicListener: " + req);

				process(req);

			} else {
				log.error("Can't verify request: " + req);
			}

		} catch (JMSException e) {
			log.error("Error: ", e);
		}
	}

	/**
	 * MQ Listener
	 */
	public void process(final GetNodeAccountsRequest t) {

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
