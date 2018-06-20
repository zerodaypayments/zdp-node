package io.zdp.node.service.validation.settle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.zdp.node.service.validation.cache.LockedAccountsCache;
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

	public void settle(TransferSettlementRequest req) {

		log.debug("Settle: " + req);

		updateAccounts(req);

		saveCurrentTransfer(req);

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
