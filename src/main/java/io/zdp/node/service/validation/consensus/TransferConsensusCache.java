package io.zdp.node.service.validation.consensus;

import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import io.zdp.node.service.validation.cache.key.ByteWrapper;
import io.zdp.node.service.validation.model.UnconfirmedTransfer;

/**
 * 
 * Consensus in process
 * 
 * @author sn_1970@yahoo.com
 *
 */
@Service
public class TransferConsensusCache {

	private Cache<ByteWrapper, UnconfirmedTransfer> cache;

	@PostConstruct
	public void init() {
		cache = CacheBuilder.newBuilder().maximumSize(10000000L).expireAfterWrite(10, TimeUnit.MINUTES).build();
	}

	public void add(UnconfirmedTransfer t) {
		cache.put(new ByteWrapper(t.getTransactionSignature()), t);
	}

	public boolean contains(UnconfirmedTransfer t) {
		return cache.getIfPresent(new ByteWrapper(t.getTransactionSignature())) != null;
	}

}
