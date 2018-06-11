package io.zdp.node.service.validation.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.zdp.crypto.account.ZDPAccountUuid;
import io.zdp.model.network.NetworkTopologyService;
import io.zdp.node.service.validation.LockedAccountsCache;
import io.zdp.node.service.validation.model.TransferConfirmationResponse;
import io.zdp.node.storage.account.domain.Account;
import io.zdp.node.storage.account.service.AccountService;
import io.zdp.node.storage.transfer.dao.TransferHeaderDao;

@Component(value="transferConfirmationListener")
public class TransferConfirmationListener {

	private final Logger log=LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private LockedAccountsCache accountsInProgressCache;
	
	@Autowired
	private NetworkTopologyService networkService;

	@Autowired
	private AccountService accountService;

	@Autowired
	private TransferHeaderDao transferHeaderDao;
	
	public void process(TransferConfirmationResponse req) {
		/*
		log.debug("Transfer confirmation: " + req);

		log.debug("Prepare transfer: " + req);

		// Validate request (otherwise malicious actors can start locking accounts)
		if (false == networkService.isValidServerRequest(req.getServerUuid(), req.toHashData(), req.getSignedRequest())) {
			return new TransferConfirmation(Status.UNAUTHORIZED);
		}

		final ZDPAccountUuid from = new ZDPAccountUuid(req.getFromAccountUuid());
		final ZDPAccountUuid to = new ZDPAccountUuid(req.getToAccountUuid());

		// Transaction replay?
		if (transferHeaderDao.findByUuid(req.getRawTransferUuid()) != null) {
			return new TransferConfirmation(Status.REPLAY_DETECTED);
		}

		// If in 'current transactions' cache on any of the nodes -> stop
		if (accountsInProgressCache.inProgress(req.getFromAccountUuid())) {
			return new TransferConfirmation(Status.ACCOUNT_LOCKED);
		}
		if (accountsInProgressCache.inProgress(req.getToAccountUuid())) {
			return new TransferConfirmation(Status.ACCOUNT_LOCKED);
		}

		// Load account from local storage
		Account fromAccount = this.accountService.findByUuid(from.getPublicKeyHash());
		Account toAccount = this.accountService.findByUuid(to.getPublicKeyHash());

		// Start transaction
		accountsInProgressCache.add(req.getFromAccountUuid());
		accountsInProgressCache.add(req.getToAccountUuid());

		TransferConfirmation resp = new TransferConfirmation(Status.APPROVED);
		resp.setFromAccount(fromAccount);
		resp.setToAccount(toAccount);
*/
		
	}
	
}
