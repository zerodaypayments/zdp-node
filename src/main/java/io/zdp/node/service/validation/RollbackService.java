package io.zdp.node.service.validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.zdp.node.service.network.NetworkTopologyService;
import io.zdp.node.web.api.validation.model.ValidationPrepareTransferRequest;

@Service
public class RollbackService {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private LockedAccountsCache accountsInProgressCache;

	@Autowired
	private NetworkTopologyService networkService;

	public boolean rollback(ValidationPrepareTransferRequest req) {

		log.debug("rollback transfer: " + req);

		// Validate request (otherwise malicious actors can start unlocking accounts)
		if (false == networkService.isValidServerRequest(req.getServerUuid(), req.toHashData(), req.getSignedRequest())) {
			return false;
		}

		accountsInProgressCache.remove(req.getFromAccountUuid());
		accountsInProgressCache.remove(req.getToAccountUuid());

		return true;

	}

}
