package io.zdp.node.service.validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.zdp.node.service.NodeConfigurationService;
import io.zdp.node.service.network.NetworkTopologyService;
import io.zdp.node.storage.account.domain.Account;
import io.zdp.node.storage.account.service.AccountService;
import io.zdp.node.storage.transfer.domain.TransferHeader;
import io.zdp.node.storage.transfer.service.TransferHeaderService;
import io.zdp.node.web.api.validation.model.ValidationCommitRequest;
import io.zdp.node.web.api.validation.model.ValidationCommitResponse;

@Service
public class CommitService {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private ActiveAccountsCache accountsInProgressCache;

	@Autowired
	private NetworkTopologyService networkService;

	@Autowired
	private AccountService accountService;

	@Autowired
	private TransferHeaderService transferHeaderService;

	@Autowired
	private NodeConfigurationService nodeConfigService;

	@Transactional(readOnly = false)
	public boolean commit(ValidationCommitRequest req) {

		updateAccount(req);

		saveTransferHeader(req);

		return true;

	}

	private void saveTransferHeader(ValidationCommitRequest req) {
		transferHeaderService.save(new TransferHeader(req.getTransferSignature()));
	}

	private void updateAccount(ValidationCommitRequest req) {
		// Update or save account;
		Account a = accountService.findByUuid(req.getAccount().getUuidAsBytes());

		if (a == null) {
			a = new Account();
			a.setCurve(req.getAccount().getCurve());
			a.setUuid(req.getAccount().getUuidAsBytes());
		}

		a.setBalance(req.getAccount().getBalance());
		a.setHeight(req.getAccount().getHeight());
		a.setTransferHash(req.getAccount().getTransferHash());

		this.accountService.save(a);
	}

}
