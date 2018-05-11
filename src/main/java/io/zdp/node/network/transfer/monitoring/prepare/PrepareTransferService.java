package io.zdp.node.network.transfer.monitoring.prepare;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.zdp.node.network.topology.NetworkNodeService;
import io.zdp.node.network.transfer.monitoring.AccountsInProgressCache;
import io.zdp.node.network.transfer.monitoring.prepare.PrepareTransferResponse.Status;

@Service
public class PrepareTransferService {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private AccountsInProgressCache accountsInProgressCache;

	@Autowired
	private NetworkNodeService networkService;
	
	public PrepareTransferResponse prepare(PrepareTransferRequest req) {

		log.debug("Prepare transfer: " + req);

		final PrepareTransferResponse resp = new PrepareTransferResponse();
		resp.setResponseUuid(req.getTransferUuid());
		
		// Sign response
		//networkService.signResponse()
		

		// Validate request origin
		if (false == networkService.isValidServerRequest(req.getServerUuid(), req.toSignature(), req.getServerSignature())) {
			resp.setStatus(Status.REJECTED_NOT_VALID);
			return resp;
		}

		resp.setStatus(Status.APPROVED);
		return resp;

	}

}
