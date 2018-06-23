package io.zdp.node.service.validation.balance;

import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

import io.zdp.crypto.Base58;
import io.zdp.node.service.validation.cache.key.ByteWrapper;
import io.zdp.node.storage.account.domain.Account;
import io.zdp.node.storage.account.service.AccountService;

/**
 * 
 * @author sn_1970@yahoo.com
 *
 */
@Service
public class BalanceRequestCache {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	private Cache<ByteWrapper, BalanceRequest> cache;

	@Autowired
	private AccountService accountService;

	@Autowired
	private AccountBalanceCache accountBalanceCache;

	@PostConstruct
	public void init() {

		final RemovalListener<ByteWrapper, BalanceRequest> listener = new RemovalListener<ByteWrapper, BalanceRequest>() {

			@Override
			@Transactional
			public void onRemoval(RemovalNotification<ByteWrapper, BalanceRequest> entry) {

				final BalanceRequest request = entry.getValue();

				log.debug("Finalizing account balance: " + Base58.encode(request.getAccountUuid()));

				// Account account = accountService.findByUuid(request.getAccountUuid());

				Account account = null;

				for (final BalanceResponse resp : request.getResponses()) {

					if (account == null || account.getHeight() < resp.getAccount().getHeight()) {
						account = resp.getAccount();
					}

				}

				Account existing = accountService.findByUuid(request.getAccountUuid());

				if (existing == null) {
					existing = new Account();
					existing.setCurve(account.getCurve());
					existing.setUuid(account.getUuidAsBytes());
				}

				existing.setBalance(account.getBalance());
				existing.setHeight(account.getHeight());
				existing.setTransferHash(account.getTransferHash());

				accountBalanceCache.add(existing.getUuidAsBytes(), existing);

				accountService.save(existing);

			}
		};

		cache = CacheBuilder.newBuilder().removalListener(listener).maximumSize(10000000L).expireAfterWrite(3, TimeUnit.SECONDS).build();
	}

	public BalanceRequest get(byte[] uuid) {
		return cache.getIfPresent(new ByteWrapper(uuid));
	}

	public void add(byte[] uuid, BalanceRequest req) {
		log.debug("Got balance request for: " + Base58.encode(uuid));
		cache.put(new ByteWrapper(uuid), req);
	}

	public boolean contains(byte[] uuid) {
		return cache.getIfPresent(new ByteWrapper(uuid)) != null;
	}

}
