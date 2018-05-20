package io.zdp.node.service.validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.zdp.node.service.network.NetworkTopologyService;
import io.zdp.node.storage.account.domain.Account;
import io.zdp.node.storage.account.service.AccountService;
import io.zdp.node.storage.transfer.dao.CurrentTransferDao;
import io.zdp.node.storage.transfer.domain.TransferHeader;
import io.zdp.node.storage.transfer.service.TransferHeaderService;
import io.zdp.node.web.api.validation.model.ValidationCommitRequest;

@Service
public class CommitService {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private LockedAccountsCache accountsInProgressCache;

	@Autowired
	private NetworkTopologyService networkService;

	@Autowired
	private AccountService accountService;

	@Autowired
	private TransferHeaderService transferHeaderService;

	@Autowired
	private CurrentTransferDao currentTransferDao;

	@Transactional(readOnly = false)
	public boolean commit(ValidationCommitRequest req) {

		log.debug("Commit request: " + req);

		// Validate request (otherwise malicious actors can start locking accounts)
		if (false == networkService.isValidServerRequest(req.getServerUuid(), req.toHashData(), req.getRequestSignature())) {
			return false;
		}

		updateAccounts(req);

		saveTransferHeader(req);

		saveCurrentTransfer(req);

		accountsInProgressCache.remove(req.getFromAccount().getUuid());
		accountsInProgressCache.remove(req.getToAccount().getUuid());

		return true;

	}

	private void saveCurrentTransfer(ValidationCommitRequest req) {

		log.debug("saveCurrentTransfer: " + req.getFromAccount());

		currentTransferDao.save(req.getTransfer());

	}

	private void saveTransferHeader(ValidationCommitRequest req) {
		transferHeaderService.save(new TransferHeader(req.getTransferSignature()));
	}

	private void updateAccounts(ValidationCommitRequest req) {

		{
			// Update or save FROM account;
			Account from = accountService.findByUuid(req.getFromAccount().getUuidAsBytes());

			if (from == null) {
				from = new Account();
				from.setCurve(req.getFromAccount().getCurve());
				from.setUuid(req.getToAccount().getUuidAsBytes());
			}

			from.setBalance(req.getFromAccount().getBalance());
			from.setHeight(req.getFromAccount().getHeight());
			from.setTransferHash(req.getFromAccount().getTransferHash());

			log.debug("Updated FROM account: " + req.getFromAccount());

			this.accountService.save(from);

		}

		{
			// to account
			Account to = accountService.findByUuid(req.getToAccount().getUuidAsBytes());

			if (to == null) {
				to = new Account();
				to.setCurve(req.getToAccount().getCurve());
				to.setUuid(req.getToAccount().getUuidAsBytes());
			}

			to.setBalance(req.getToAccount().getBalance());
			to.setHeight(req.getToAccount().getHeight());
			to.setTransferHash(req.getToAccount().getTransferHash());

			log.debug("Updated TO account: " + req.getToAccount());

			this.accountService.save(to);
		}

	}

}
