package io.zdp.node.service.validation.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.zdp.api.model.v1.TransferRequest;
import io.zdp.crypto.account.ZDPAccountUuid;
import io.zdp.model.network.NetworkTopologyService;
import io.zdp.node.service.validation.LockedAccountsCache;
import io.zdp.node.service.validation.model.TransferConfirmationResponse;
import io.zdp.node.service.validation.model.TransferConfirmationResponse.Status;
import io.zdp.node.storage.account.domain.Account;
import io.zdp.node.storage.account.service.AccountService;
import io.zdp.node.storage.transfer.dao.TransferHeaderDao;

@Component(value = "newTransferListener")
public class NewTransferListener {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private NetworkTopologyService networkService;

	@Autowired
	private AccountService accountService;

	@Autowired
	private TransferHeaderDao transferHeaderDao;

	@Autowired
	private LockedAccountsCache lockedAccountsCache;

	@Autowired
	private TransferConfirmationGateway transferConfirmationGateway;

	public void process(TransferRequest req) {
/*
		log.debug("New transfer: " + req);

		final ZDPAccountUuid from = new ZDPAccountUuid(req.getFromAccountUuid());
		final ZDPAccountUuid to = new ZDPAccountUuid(req.getToAccountUuid());

		// Validate request (otherwise malicious actors can start locking accounts)
		if (false == networkService.isValidServerRequest(req.getServerUuid(), req.toHashData(), req.getSignedRequest())) {
			transferConfirmationGateway.send(new TransferConfirmation(TransferConfirmation.Status.UNAUTHORIZED));
			return;
		}

		// Transaction replay?
		if (transferHeaderDao.findByUuid(req.getRawTransferUuid()) != null) {
			transferConfirmationGateway.send(new TransferConfirmation(Status.REPLAY_DETECTED));
			return;
		}

		// If in 'current transactions' cache on any of the nodes -> stop
		if (lockedAccountsCache.inProgress(req.getFromAccountUuid())) {
			transferConfirmationGateway.send(new TransferConfirmation(Status.ACCOUNT_LOCKED));
			return;
		}

		if (lockedAccountsCache.inProgress(req.getToAccountUuid())) {
			transferConfirmationGateway.send(new TransferConfirmation(Status.ACCOUNT_LOCKED));
			return;
		}

		// Load account from local storage
		Account fromAccount = this.accountService.findByUuid(from.getPublicKeyHash());
		Account toAccount = this.accountService.findByUuid(to.getPublicKeyHash());

		// Start transaction
		lockedAccountsCache.add(req.getFromAccountUuid());
		lockedAccountsCache.add(req.getToAccountUuid());

		TransferConfirmation resp = new TransferConfirmation(Status.APPROVED);
		resp.setFromAccount(fromAccount);
		resp.setToAccount(toAccount);

		transferConfirmationGateway.send(resp);
*/
	}

}
