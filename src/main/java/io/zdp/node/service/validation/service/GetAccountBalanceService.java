package io.zdp.node.service.validation.service;

import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import io.zdp.api.model.v1.GetBalanceRequest;
import io.zdp.crypto.account.ZDPAccountUuid;
import io.zdp.node.service.validation.balance.AccountBalanceCache;
import io.zdp.node.service.validation.balance.BalanceRequest;
import io.zdp.node.service.validation.balance.BalanceRequestCache;
import io.zdp.node.service.validation.balance.BalanceRequestResolver;
import io.zdp.node.service.validation.balance.BalanceRequestTopicPublisher;
import io.zdp.node.service.validation.balance.BalanceResponse;
import io.zdp.node.storage.account.dao.AccountDao;
import io.zdp.node.storage.account.domain.Account;

@Service
public class GetAccountBalanceService {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private AccountDao accountDao;

	@Autowired
	private BalanceRequestCache balanceRequestCache;

	@Autowired
	private BalanceRequestTopicPublisher getBalanceRequestTopicPublisher;

	@Autowired
	private ValidationNodeSigner validationNodeSigner;

	@Autowired
	private AccountBalanceCache accountBalanceCache;

	@Autowired
	private BalanceRequestResolver resolver;

	@Async
	public Future<Account> getBalance(final GetBalanceRequest request, final ZDPAccountUuid accountUuid) throws Exception {

		// is in process? return
		if (balanceRequestCache.contains(accountUuid.getPublicKeyHash())) {
			log.warn("Balance request in progress, return");
			return null;
		}

		final long st = System.currentTimeMillis();

		// Create balance request to broadcast
		BalanceRequest balanceRequest = new BalanceRequest();
		balanceRequest.setAccountUuid(accountUuid.getPublicKeyHash());
		validationNodeSigner.sign(balanceRequest);

		log.debug("Balance request: " + balanceRequest);

		// Broadcast
		getBalanceRequestTopicPublisher.send(balanceRequest);

		log.debug("Balance request broadcasted: " + balanceRequest);

		// Save to local cache storage
		balanceRequestCache.add(accountUuid.getPublicKeyHash(), balanceRequest);

		log.debug("Balance saved to local store: " + balanceRequest);

		// Get local account
		final Account account = this.accountDao.findByUuid(accountUuid.getPublicKeyHash());
		if (account != null) {
			balanceRequest.getResponses().add(new BalanceResponse(account));
		} else {
			balanceRequest.getResponses().add(new BalanceResponse(accountUuid.getPublicKeyHash()));
		}

		log.debug("Sleep for 5 seconds");

		Thread.sleep(5000);

		long et = System.currentTimeMillis();

		synchronized (balanceRequest) {
			if (balanceRequest.isResolved() == false) {
				resolver.resolve(balanceRequest);
			}
		}

		log.debug("Getting balance took: " + (et - st) + " ms.");

		Account acc = this.accountBalanceCache.get(accountUuid.getPublicKeyHash());

		log.debug("Acc: " + acc);

		return new AsyncResult<Account>(acc);

	}

}
