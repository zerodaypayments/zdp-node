package io.zdp.node.service.validation.cache;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import io.zdp.node.service.validation.cache.key.ByteWrapper;

/**
 * 
 * @author sn_1970@yahoo.com
 *
 */
@Service
public class RecentTransfersCache {

	private Cache<ByteWrapper, Boolean> cache;

	@PostConstruct
	public void init() {
		cache = CacheBuilder.newBuilder().maximumSize(10000000L).expireAfterWrite(1, TimeUnit.MINUTES).build();
	}

	public void add(byte[] uuid) {
		cache.put(new ByteWrapper(uuid), Boolean.TRUE);
	}

	public boolean contains(byte[] uuid) {
		return cache.getIfPresent(new ByteWrapper(uuid)) != null;
	}

	public Set<ByteWrapper> findAll() {
		return cache.asMap().keySet();
	}

}
