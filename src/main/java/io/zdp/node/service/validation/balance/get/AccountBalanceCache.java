package io.zdp.node.service.validation.balance.get;

import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import io.zdp.node.service.validation.cache.key.ByteWrapper;
import io.zdp.node.storage.account.domain.Account;

/**
 * Cache account balances for 10 seconds
 * 
 * @author sn_1970@yahoo.com
 */
@Service
public class AccountBalanceCache {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	private Cache<ByteWrapper, Account> cache;

	@PostConstruct
	public void init() {

		cache = CacheBuilder.newBuilder().maximumSize(100000L).expireAfterWrite(30, TimeUnit.SECONDS).build();
	}

	public void add(byte[] uuid, Account acc) {
		log.debug("Add account to local balance cache: " + acc);
		cache.put(new ByteWrapper(uuid), acc);
	}

	public boolean contains(byte[] uuid) {
		return cache.getIfPresent(new ByteWrapper(uuid)) != null;
	}

	public Account get(byte[] uuid) {
		return cache.getIfPresent(new ByteWrapper(uuid));
	}

}