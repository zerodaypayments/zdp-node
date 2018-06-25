package io.zdp.node.service.validation.balance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.zdp.crypto.Base58;
import io.zdp.node.storage.account.domain.Account;
import io.zdp.node.storage.account.service.AccountService;

@Service
public class BalanceRequestResolver {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private AccountService accountService;

	@Autowired
	private AccountBalanceCache accountBalanceCache;

	public void resolve(final BalanceRequest request) {

		Account existing = null;

		synchronized (request) {

			if (false == request.isResolved()) {

				log.debug("Finalizing account balance: " + Base58.encode(request.getAccountUuid()));

				Account account = null;

				for (final BalanceResponse resp : request.getResponses()) {

					if (resp.getAccount() != null) {

						if (account == null || account.getHeight() < resp.getAccount().getHeight()) {
							account = resp.getAccount();
						}
					}

				}

				existing = accountService.findByUuid(request.getAccountUuid());

				// TODO review
				if (account != null && existing!=null) {
					// TODO review
					existing.setBalance(account.getBalance());
					existing.setHeight(account.getHeight());
					existing.setTransferHash(account.getTransferHash());
					// TODO review
				} else if (account!=null && existing==null)
					// TODO review				
				if (existing == null) {
					existing = new Account();
					existing.setCurve(account.getCurve());
					existing.setUuid(account.getUuidAsBytes());
				}
				// TODO review
				if (existing!=null) {
					accountBalanceCache.add(existing.getUuidAsBytes(), existing);
					accountService.save(existing);
				}
				// TODO review

			}

			log.debug("Balance finalized: " + existing);

			request.setResolved(true);

		}

	}

}
