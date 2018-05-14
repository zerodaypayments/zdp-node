package io.zdp.node.network.server.tx;

import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

@Component
public class AccountTxCache {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	private Cache<String, Boolean> cache;

	@PostConstruct
	public void init() {

		RemovalListener<String, Boolean> listener = new RemovalListener<String, Boolean>() {

			@Override
			public void onRemoval(RemovalNotification<String, Boolean> notification) {
				log.debug("Transfer " + notification.getKey() + " removed");
			}

		};
		cache = CacheBuilder.newBuilder().removalListener(listener).maximumSize(10000000L).expireAfterWrite(5, TimeUnit.SECONDS).build();

	}

	public void add(String accountUuid) {

		if (inProgress(accountUuid)) {
			throw new RuntimeException("Account alread in cache: " + accountUuid);
		}

		cache.put(accountUuid, true);

		log.debug("Account added: " + accountUuid);

	}

	public boolean inProgress(String accountUuid) {

		return cache.getIfPresent(accountUuid) != null;

	}

	public void remove(String accountUuid) {

		if (inProgress(accountUuid)) {
			cache.invalidate(accountUuid);
			log.debug("Account removed: " + accountUuid);
		}
	}

}
