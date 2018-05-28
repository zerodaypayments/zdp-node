package io.zdp.node.storage.account.service;

import java.math.BigDecimal;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.zdp.api.model.v1.GetBalanceRequest;
import io.zdp.api.model.v1.GetBalanceResponse;
import io.zdp.crypto.Base58;
import io.zdp.crypto.Curves;
import io.zdp.crypto.account.ZDPAccountUuid;
import io.zdp.node.storage.account.dao.AccountDao;
import io.zdp.node.storage.account.domain.Account;

@Service
public class AccountService {

	private final Logger log = LoggerFactory.getLogger( this.getClass() );

	@Autowired
	private AccountDao accountDao;

	@PostConstruct
	public void init ( ) throws Exception {
		if ( accountDao.count() == 0 ) {

			log.debug( "Seems like a fresh node start!" );

			Account genesis = new Account();
			genesis.setBalance( new BigDecimal( 90000000000L ) );
			genesis.setCurve( Curves.DEFAULT_CURVE_INDEX );
			genesis.setHeight( 1 );
			genesis.setTransferHash( new byte [ ] {} );
			genesis.setUuid( Base58.decode( "NDmQbwTw3gVT6uihcQjcoRCsNG7" ) );

			// mint a genesis account
			accountDao.save( genesis );

			log.info( "Just minted a genesis account, how exciting is that????" );
			log.info( genesis.toString() );

		}
	}

	@Transactional ( readOnly = false )
	public GetBalanceResponse getBalance ( GetBalanceRequest request ) throws Exception {

		long st = System.currentTimeMillis();

		final GetBalanceResponse resp = new GetBalanceResponse();

		// Generate account from public key
		final byte [ ] accountUuid = new ZDPAccountUuid( request.getAccountUuid() ).getPublicKeyHash();

		Account account = this.accountDao.findByUuid( accountUuid );

		if ( account != null ) {
			resp.setAmount( account.getBalance().toPlainString() );
			resp.setHeight( account.getHeight() );
			resp.setChainHash( account.getTransferHash() );
		} else {
			resp.setAmount( "0.0" );
		}

		long et = System.currentTimeMillis();

		log.debug( "Getting balance took: " + ( et - st ) + " ms." );

		return resp;
	}

	@Transactional ( readOnly = true )
	public long countAccounts ( ) {
		return this.accountDao.count();
	}

	@Transactional ( readOnly = true )
	public Account findByUuid ( byte [ ] uuid ) {
		return this.accountDao.findByUuid( uuid );
	}

	@Transactional ( readOnly = false )
	public Account save ( Account a ) {
		return this.accountDao.save( a );
	}

}
