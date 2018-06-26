package io.zdp.node.service.validation.balance.get;

import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

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
	private BalanceRequestResolver resolver;

	@PostConstruct
	public void init() {

		final RemovalListener<ByteWrapper, BalanceRequest> listener = new RemovalListener<ByteWrapper, BalanceRequest>() {

			@Override
			public void onRemoval(RemovalNotification<ByteWrapper, BalanceRequest> entry) {

				BalanceRequest req = entry.getValue();

				log.debug("Expiring balance request: " + req);

				synchronized (req) {
					if (false == req.isResolved()) {
						resolver.resolve(req);
					}
				}
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
