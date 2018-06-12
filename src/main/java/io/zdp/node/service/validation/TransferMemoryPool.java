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

import io.zdp.node.service.validation.model.UnconfirmedTransfer;

/**
 * 
 * Container for unconfirmed transactions
 * 
 * @author sn_1970@yahoo.com
 *
 */
@Service
public class TransferMemoryPool {

	private final Logger log = LoggerFactory.getLogger( this.getClass() );

	private Cache < String, UnconfirmedTransfer > cache;

	@PostConstruct
	public void init ( ) {

		RemovalListener < String, UnconfirmedTransfer > listener = new RemovalListener < String, UnconfirmedTransfer >() {

			@Override
			public void onRemoval ( RemovalNotification < String, UnconfirmedTransfer > notification ) {
				log.debug( "Transfer " + notification.getKey() + " removed from memory pool" );
			}

		};

		cache = CacheBuilder.newBuilder().removalListener( listener ).maximumSize( 10000000L ).expireAfterWrite( 1, TimeUnit.MINUTES ).build();

	}

	@Scheduled ( fixedDelay = DateUtils.MILLIS_PER_SECOND * 4 )
	public void log ( ) {
		log.debug( "Memory pool size: " + cache.size() );
	}

	public void add ( UnconfirmedTransfer c ) {
		cache.put( c.getTransactionUuid(), c );
	}

	public UnconfirmedTransfer get ( String uuid ) {
		return cache.getIfPresent( uuid );
	}

}
