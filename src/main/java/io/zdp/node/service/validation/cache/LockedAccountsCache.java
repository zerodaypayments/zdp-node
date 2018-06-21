package io.zdp.node.service.validation.cache;

import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

import io.zdp.node.service.validation.cache.key.ByteWrapper;

@Component
public class LockedAccountsCache {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	private Cache<ByteWrapper, Boolean> cache;

	@PostConstruct
	public void init() {

		RemovalListener<ByteWrapper, Boolean> listener = new RemovalListener<ByteWrapper, Boolean>() {

			@Override
			public void onRemoval(RemovalNotification<ByteWrapper, Boolean> notification) {
				log.debug("Transfer " + notification.getKey() + " removed");
			}

		};
		cache = CacheBuilder.newBuilder().removalListener(listener).maximumSize(10000000L).expireAfterWrite(10, TimeUnit.SECONDS).build();

	}

	public void add(byte[] accountUuid) {

		if (inProgress(accountUuid)) {
			throw new RuntimeException("Account alread in cache: " + accountUuid);
		}

		ByteWrapper bw = new ByteWrapper(accountUuid);

		cache.put(bw, true);

		log.debug("Account added: " + bw);

	}

	public boolean inProgress(byte[] accountUuid) {

		return cache.getIfPresent(new ByteWrapper(accountUuid)) != null;

	}

	public void remove(byte[] accountUuid) {

		ByteWrapper bw = new ByteWrapper(accountUuid);

		cache.invalidate(bw);

		log.debug("Account removed: " + bw);

	}

}
