package io.zdp.node.network.server.tx.vote;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.zdp.api.model.v1.VoteRequest;
import io.zdp.api.model.v1.VoteResponse;
import io.zdp.api.model.v1.VoteResponse.Status;
import io.zdp.crypto.Signing;
import io.zdp.node.domain.Account;
import io.zdp.node.network.server.tx.AccountTxCache;
import io.zdp.node.network.topology.NetworkNodeService;
import io.zdp.node.service.AccountService;
import io.zdp.node.service.NodeConfigurationService;

@Service
public class VoteService {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private AccountTxCache accountsInProgressCache;

	@Autowired
	private NetworkNodeService networkService;

	@Autowired
	private AccountService accountService;

	@Autowired
	private NodeConfigurationService nodeConfigService;

	public VoteResponse prepare(VoteRequest req) {

		log.debug("Prepare transfer: " + req);

		// If in 'current transactions' cache on any of the nodes -> stop
		if (accountsInProgressCache.inProgress(req.getAccountUuidAsString())) {
			return resp(null, req, Status.REJECTED_ACCOUNT_IN_PROGRESS);
		}

		// Load account from local storage
		Account account = this.accountService.findByUuid(req.getAccountUuid());
		if (account == null) {
			return resp(null, req, Status.APPROVED_NO_ACCOUNT_ON_FILE);
		}

		// Start transaction
		accountsInProgressCache.add(req.getAccountUuidAsString());

		VoteResponse resp = resp(account, req, Status.APPROVED);

		return resp;

	}

	private VoteResponse resp(Account a, VoteRequest req, Status status) {

		final VoteResponse resp = new VoteResponse();
		resp.setStatus(status);
		resp.setRequestUuid(req.getRequestUuid());

		if (a != null) {

			resp.setBalance(a.getBalance().toPlainString());
			resp.setHeight(a.getHeight());
			resp.setTransferChainHash(a.getTransferHash());
		}

		try {
			byte[] signature = Signing.sign(nodeConfigService.getNode().getECPrivateKey(), resp.toHashData());
			resp.setSignedRequestUuid(signature);
		} catch (Exception e) {
			log.error("Error: ", e);
		}

		return resp;

	}

}
