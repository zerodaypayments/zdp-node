package io.zdp.node.service.validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.zdp.api.model.v1.VoteRequest;
import io.zdp.api.model.v1.VoteResponse;
import io.zdp.api.model.v1.VoteResponse.Status;
import io.zdp.crypto.Signing;
import io.zdp.node.service.NodeConfigurationService;
import io.zdp.node.service.network.NetworkTopologyService;
import io.zdp.node.storage.account.domain.Account;
import io.zdp.node.storage.account.service.AccountService;
import io.zdp.node.storage.transfer.dao.TransferHeaderDao;

@Service
public class VoteService {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private ActiveAccountsCache accountsInProgressCache;

	@Autowired
	private NetworkTopologyService networkService;

	@Autowired
	private AccountService accountService;

	@Autowired
	private TransferHeaderDao transferHeaderDao;

	@Autowired
	private NodeConfigurationService nodeConfigService;

	@Transactional(readOnly = true)
	public VoteResponse prepare(VoteRequest req) {

		log.debug("Prepare transfer: " + req);

		// Transaction replay?
		if (transferHeaderDao.findByUuid(req.getTransferUuid()) != null) {
			return resp(null, req, Status.REJECTED_REPLAY_DETECTED);
		}

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
