package io.zdp.node.service.validation.balance.update;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.zdp.node.service.validation.cache.LockedAccountsCache;
import io.zdp.node.storage.account.domain.Account;
import io.zdp.node.storage.account.service.AccountService;

@Service
public class UpdateBalanceService {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private AccountService accountService;

	@Autowired
	private LockedAccountsCache lockedAccountsCache;

	@Transactional(readOnly = false)
	public void update(UpdateBalanceRequest req) {

		if (lockedAccountsCache.inProgress(req.getAccountUuid())) {

			log.warn("Account locked, no updates allowed");

			return;

		}

		Account acc = this.accountService.findByUuid(req.getAccountUuid());

		if (acc == null) {

			acc = Account.clone(req.getAccount());

		} else {

			if (acc.getHeight() < req.getAccount().getHeight()) {

				log.debug("Update account");

				acc.updateFromAccount(req.getAccount());

			}

		}

		this.accountService.save(acc);

		// TODO publish to monitoring network via topic

	}

}
