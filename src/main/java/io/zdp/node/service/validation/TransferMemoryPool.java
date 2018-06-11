package io.zdp.node.service.validation;

import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

import io.zdp.node.service.validation.model.TransferConfirmationResponse;

/**
 * 
 * Container for unconfirmed transactions
 * 
 * @author sn_1970@yahoo.com
 *
 */
@Service
public class TransferMemoryPool {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private Cache<String, TransferConfirmationResponse> cache;

	@PostConstruct
	public void init() {

		RemovalListener<String, TransferConfirmationResponse> listener = new RemovalListener<String, TransferConfirmationResponse>() {

			@Override
			public void onRemoval(RemovalNotification<String, TransferConfirmationResponse> notification) {
				log.debug("Transfer " + notification.getKey() + " removed from memory pool");
			}

		};

		cache = CacheBuilder.newBuilder().removalListener(listener).maximumSize(10000000L).expireAfterWrite(1, TimeUnit.MINUTES).build();

	}

	@Scheduled(fixedDelay = DateUtils.MILLIS_PER_SECOND * 4)
	public void log() {
		log.debug("Memory pool size: " + cache.size());
	}

	public void add(TransferConfirmationResponse c) {
		cache.put(c.getTransferUuid(), c);
	}

	public TransferConfirmationResponse get(String uuid) {
		return cache.getIfPresent(uuid);
	}

}
