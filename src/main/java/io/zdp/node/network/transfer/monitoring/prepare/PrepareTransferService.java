package io.zdp.node.network.transfer.monitoring.prepare;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.zdp.crypto.Signing;
import io.zdp.node.network.topology.NetworkNodeService;
import io.zdp.node.network.transfer.monitoring.AccountsInProgressCache;
import io.zdp.node.network.transfer.monitoring.prepare.PrepareTransferResponse.Status;
import io.zdp.node.service.NodeConfigurationService;

@Service
public class PrepareTransferService {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private AccountsInProgressCache accountsInProgressCache;

	@Autowired
	private NetworkNodeService networkService;

	@Autowired
	private NodeConfigurationService nodeConfigService;

	public PrepareTransferResponse prepare(PrepareTransferRequest req) {

		log.debug("Prepare transfer: " + req);

		// Validate request origin (based on server request signature)
		if (false == networkService.isValidServerRequest(req.getServerUuid(), req.toSignature(), req.getServerSignature())) {
			return resp(req, Status.REJECTED_NOT_VALID);
		}

		// If in 'current transactions' cache on any of the nodes -> stop
		if (accountsInProgressCache.inProgress(req.getFrom())) {
			return resp(req, Status.REJECTED_ACCOUNT_IN_PROGRESS);
		}
		
		
		// Start transaction
		accountsInProgressCache.add(req.getFrom());

		return resp(req, Status.APPROVED);

	}

	private PrepareTransferResponse resp(PrepareTransferRequest req, Status status) {

		final PrepareTransferResponse resp = new PrepareTransferResponse();
		resp.setResponseUuid(req.getRequestUuid());
		resp.setStatus(status);
		resp.setTransferUuid(req.getTransferUuid());

		// Sign response
		resp.setServerUuid(nodeConfigService.getNode().getUuid());

		try {
			byte[] signature = Signing.sign(nodeConfigService.getNode().getECPrivateKey(), resp.toSignatureData());
			resp.setServerSignature(signature);
		} catch (Exception e) {
			log.error("Error: ", e);
		}

		return resp;

	}

}
