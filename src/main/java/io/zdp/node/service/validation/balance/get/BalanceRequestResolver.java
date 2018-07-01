package io.zdp.node.service.validation.balance.get;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.zdp.crypto.Base58;
import io.zdp.node.service.validation.balance.update.UpdateBalanceRequest;
import io.zdp.node.service.validation.balance.update.UpdateBalanceRequestValidationTopicPublisher;
import io.zdp.node.service.validation.balance.update.UpdateBalanceService;
import io.zdp.node.storage.account.domain.Account;

@Service
public class BalanceRequestResolver {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private UpdateBalanceService updateBalanceService;

	@Autowired
	private AccountBalanceCache accountBalanceCache;

	@Autowired
	private UpdateBalanceRequestValidationTopicPublisher updateBalanceRequestValidationTopicPublisher;

	public void resolve(final BalanceRequest request) {

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

				if (account == null) {
					log.warn("Looks like account cannot be found as it doesnt exist in the network yet");
					request.setResolved(true);
					return;
				}

				log.debug("Account for balance finalized: " + account);

				this.accountBalanceCache.add(account.getUuidAsBytes(), account);

				UpdateBalanceRequest updateBalanceRequest = new UpdateBalanceRequest(account);

				// Create or update account locally
				updateBalanceService.update(updateBalanceRequest);

				// Let validation network know
				updateBalanceRequestValidationTopicPublisher.send(updateBalanceRequest);

			}

			request.setResolved(true);

		}

	}

}
