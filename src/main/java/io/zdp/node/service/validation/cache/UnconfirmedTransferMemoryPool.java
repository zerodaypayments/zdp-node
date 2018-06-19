package io.zdp.node.service.validation.cache;

import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

import io.zdp.node.service.validation.cache.key.ByteWrapper;
import io.zdp.node.service.validation.model.UnconfirmedTransfer;
import io.zdp.node.service.validation.service.TransferConsensusService;

/**
 * 
 * Container for unconfirmed transactions
 * 
 * @author sn_1970@yahoo.com
 *
 */
@Service
public class UnconfirmedTransferMemoryPool {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private TransferConsensusService transferConsensusService;

	private Cache<ByteWrapper, UnconfirmedTransfer> cache;

	@PostConstruct
	public void init() {

		RemovalListener<ByteWrapper, UnconfirmedTransfer> listener = new RemovalListener<ByteWrapper, UnconfirmedTransfer>() {

			@Override
			public void onRemoval(RemovalNotification<ByteWrapper, UnconfirmedTransfer> notification) {

				if (notification.getValue().isReadyToSettle()) {
					transferConsensusService.settle(notification.getValue());
				}

				log.debug("Transfer " + notification.getKey() + " removed from memory pool");

			}

		};

		cache = CacheBuilder.newBuilder().removalListener(listener).maximumSize(10000000L).expireAfterWrite(1, TimeUnit.MINUTES).build();

	}

	@Scheduled(fixedDelay = DateUtils.MILLIS_PER_SECOND * 4)
	public void log() {
		// log.debug( "Memory pool size: " + cache.size() );
	}

	public void add(UnconfirmedTransfer c) {
		cache.put(new ByteWrapper(c.getTransactionSignature()), c);
	}

	public UnconfirmedTransfer get(byte[] uuid) {
		return cache.getIfPresent(new ByteWrapper(uuid));
	}

	public void remove(UnconfirmedTransfer unconfirmedTransfer) {
		cache.invalidate(new ByteWrapper(unconfirmedTransfer.getTransactionSignature()));
	}

}
