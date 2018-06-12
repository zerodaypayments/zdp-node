package io.zdp.node.service.validation.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.zdp.crypto.Signing;
import io.zdp.node.service.NodeConfigurationService;
import io.zdp.node.service.validation.LockedAccountsCache;
import io.zdp.node.service.validation.model.TransferConfirmationRequest;
import io.zdp.node.service.validation.model.TransferConfirmationResponse;
import io.zdp.node.service.validation.model.TransferConfirmationResponse.Status;
import io.zdp.node.storage.account.domain.Account;
import io.zdp.node.storage.account.service.AccountService;
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
	private AccountService accountService;

	@Autowired
	private LockedAccountsCache lockedAccountsCache;

	@Autowired
	private ValidationNodeSigner validationNodeSigner;

	public TransferConfirmationResponse confirm(TransferConfirmationRequest t) {

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

		return confirmation;

	}

	/**
	 * Load accounts, check for replay and locked accounts and sign the response
	 * and lock accounts
	 */
	public TransferConfirmationResponse process(TransferConfirmationRequest t) {

		TransferConfirmationResponse c = new TransferConfirmationResponse();

		// Locked?
		if (lockedAccountsCache.inProgress(t.getFromAccountUuid())) {
			c.setStatus(Status.ACCOUNT_LOCKED);
			return c;
		}

		if (lockedAccountsCache.inProgress(t.getToAccountUuid())) {
			c.setStatus(Status.ACCOUNT_LOCKED);
			return c;
		}

		// Check if such a tx exists, if so, return
		if (transferHeaderDao.findByUuid(t.getTransactionUuid()) != null) {
			c.setStatus(Status.REPLAY_DETECTED);
			return c;
		}

		// Locked accounts?
		Account fromAccount = this.accountService.findByUuid(t.getFromAccountUuid());
		Account toAccount = this.accountService.findByUuid(t.getToAccountUuid());

		c.setFromAccount(fromAccount);
		c.setToAccount(toAccount);
		c.setStatus(Status.OK);
		c.setTransferUuid(t.getTransactionUuid());

		try {
			validationNodeSigner.sign(c);
		} catch (Exception e) {
			log.error("Error: ", e);
		}

		// Lock accounts
		this.lockedAccountsCache.add(fromAccount.getUuidAsBytes());
		this.lockedAccountsCache.add(toAccount.getUuidAsBytes());

		return c;
	}

}
