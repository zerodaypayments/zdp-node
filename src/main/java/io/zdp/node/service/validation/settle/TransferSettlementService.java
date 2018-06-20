package io.zdp.node.service.validation.settle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.zdp.node.service.validation.cache.LockedAccountsCache;
import io.zdp.node.service.validation.cache.RecentTransfersCache;
import io.zdp.node.storage.account.domain.Account;
import io.zdp.node.storage.account.service.AccountService;
import io.zdp.node.storage.transfer.dao.CurrentTransferDao;

@Service
public class TransferSettlementService {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private AccountService accountService;

	@Autowired
	private CurrentTransferDao currentTransferDao;

	@Autowired
	private LockedAccountsCache lockedAccountsCache;

	@Autowired
	private RecentTransfersCache recentTransfersCache;

	public void settle(TransferSettlementRequest req) {

		log.debug("Settle: " + req);

		// Create/update accounts
		updateAccounts(req);

		// Save ledger record
		saveCurrentTransfer(req);

		// Add to the recent transfer cache (for replay detection)
		recentTransfersCache.add(req.getTransferUuid());

		// Un-lock accounts
		lockedAccountsCache.remove(req.getFromAccount().getUuidAsBytes());
		lockedAccountsCache.remove(req.getToAccount().getUuidAsBytes());

	}

	private void saveCurrentTransfer(TransferSettlementRequest req) {
		currentTransferDao.save(req.getCurrentTransfer());
	}

	@Transactional(readOnly = false)
	private void updateAccounts(TransferSettlementRequest req) {

		{
			Account from = accountService.findByUuid(req.getFromAccount().getUuidAsBytes());

			if (from == null) {
				from = createNewAccount(req.getFromAccount());
				req.setFromAccount(from);
			}

			updateAccount(from, req.getFromAccount());

			log.debug("Updated FROM account: " + from);

			this.accountService.save(from);

		}

		{
			// to account
			Account to = accountService.findByUuid(req.getToAccount().getUuidAsBytes());

			if (to == null) {
				to = createNewAccount(req.getToAccount());
				req.setToAccount(to);
			}

			updateAccount(to, req.getToAccount());

			log.debug("Updated TO account: " + to);

			this.accountService.save(to);
		}

	}

	private void updateAccount(Account acc, Account other) {

	}

	private Account createNewAccount(Account other) {
		// TODO Auto-generated method stub
		return null;
	}

}
