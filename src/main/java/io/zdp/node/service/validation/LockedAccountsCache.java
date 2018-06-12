package io.zdp.node.service.validation;

import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

import io.zdp.crypto.Base58;

@Component
public class LockedAccountsCache {

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
		cache = CacheBuilder.newBuilder().removalListener(listener).maximumSize(10000000L).expireAfterWrite(10, TimeUnit.SECONDS).build();

	}

	public void add(byte[] accountUuid) {

		if (inProgress(accountUuid)) {
			throw new RuntimeException("Account alread in cache: " + accountUuid);
		}

		cache.put(Base58.encode(accountUuid), true);

		log.debug("Account added: " + accountUuid);

	}

	public boolean inProgress(byte[] accountUuid) {

		return cache.getIfPresent(Base58.encode(accountUuid)) != null;

	}

	public void remove(byte[] accountUuid) {
		cache.invalidate(Base58.encode(accountUuid));
		log.debug("Account removed: " + accountUuid);
	}

}
