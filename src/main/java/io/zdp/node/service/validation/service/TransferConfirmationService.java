package io.zdp.node.service.validation.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.zdp.crypto.Signing;
import io.zdp.node.service.NodeConfigurationService;
import io.zdp.node.service.validation.listener.TransferConfirmationGateway;
import io.zdp.node.service.validation.model.TransferConfirmationResponse;
import io.zdp.node.service.validation.model.TransferConfirmationResponse.Status;
import io.zdp.node.service.validation.model.UnconfirmedTransfer;
import io.zdp.node.storage.transfer.dao.TransferHeaderDao;

/**
 * Transfer confirmation listener for Validation nodes
 * 
 * @author sn_1970@yahoo.com
 *
 */
@Service
public class TransferConfirmationService {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private NodeConfigurationService nodeConfig;

	@Autowired
	private TransferHeaderDao transferHeaderDao;

	@Autowired
	private TransferConfirmationGateway transferConfirmationGateway;

	public TransferConfirmationResponse confirm(UnconfirmedTransfer t) {

		TransferConfirmationResponse confirmation = null;

		try {

			confirmation = this.process(t);

		} catch (Exception e) {

			log.error("Error: ", e);

			confirmation = new TransferConfirmationResponse();
			confirmation.setStatus(Status.ERROR);

		}

		confirmation.setServerUuid(nodeConfig.getNode().getUuid());

		try {
			confirmation.setServerSignature(Signing.sign(nodeConfig.getNode().getECPrivateKey(), confirmation.toHash()));
		} catch (Exception e) {
			log.error("Error: ", e);
		}

		this.transferConfirmationGateway.send(confirmation);

		return confirmation;

	}

	public TransferConfirmationResponse process(UnconfirmedTransfer t) {

		TransferConfirmationResponse c = new TransferConfirmationResponse();

		// Check if such a tx exists, if so, return
		if (transferHeaderDao.findByUuid(t.getTransactionSignature()) != null) {
			c.setStatus(Status.REPLAY_DETECTED);
			return c;
		}

		return null;
	}

}
