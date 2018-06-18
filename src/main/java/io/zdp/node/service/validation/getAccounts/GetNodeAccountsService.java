package io.zdp.node.service.validation.getAccounts;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import io.zdp.node.service.validation.LockedAccountsCache;
import io.zdp.node.service.validation.getAccounts.GetNodeAccountsResponse.Status;
import io.zdp.node.service.validation.service.ValidationNodeSigner;
import io.zdp.node.storage.account.domain.Account;
import io.zdp.node.storage.account.service.AccountService;
import io.zdp.node.storage.transfer.dao.TransferHeaderDao;

/**
 * Transfer confirmation listener for Validation nodes
 * 
 * @author sn_1970@yahoo.com
 *
 */
@Component(value = "transferConfirmationService")
public class GetNodeAccountsService {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private TransferHeaderDao transferHeaderDao;

	@Autowired
	private AccountService accountService;

	@Autowired
	private LockedAccountsCache lockedAccountsCache;

	@Autowired
	private ValidationNodeSigner validationNodeSigner;

	public GetNodeAccountsResponse process(GetNodeAccountsRequest t) {

		GetNodeAccountsResponse resp = null;

		try {

			resp = this.processRequest(t);

		} catch (Exception e) {

			log.error("Error: ", e);

			resp = new GetNodeAccountsResponse();
			resp.setStatus(Status.ERROR);

		}

		return resp;

	}

	/**
	 * Load accounts, check for replay and locked accounts and sign the response
	 * and lock accounts
	 */
	@Transactional(readOnly = true)
	private GetNodeAccountsResponse processRequest(final GetNodeAccountsRequest t) {

		final GetNodeAccountsResponse c = new GetNodeAccountsResponse();

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
		final Account fromAccount = this.accountService.findByUuid(t.getFromAccountUuid());
		final Account toAccount = this.accountService.findByUuid(t.getToAccountUuid());

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
		if (fromAccount != null) {
			this.lockedAccountsCache.add(fromAccount.getUuidAsBytes());
		}

		if (toAccount != null) {
			this.lockedAccountsCache.add(toAccount.getUuidAsBytes());
		}

		return c;
	}

}
