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
import io.zdp.node.network.validation.ValidationNetworkClient;
import io.zdp.node.storage.account.dao.AccountDao;
import io.zdp.node.storage.account.domain.Account;

@Service
public class AccountService {

	private final Logger log = LoggerFactory.getLogger( this.getClass() );

	@Autowired
	private AccountDao accountDao;

	@Autowired
	private ValidationNetworkClient validationNetworkClient;

	@PostConstruct
	public void init ( ) throws Exception {
		if ( accountDao.count() == 0 ) {

			log.debug( "Seems like a fresh node start!" );

			Account genesis = new Account();
			genesis.setBalance( new BigDecimal( 90000000000L ) );
			genesis.setCurve( Curves.DEFAULT_CURVE_INDEX );
			genesis.setHeight( 1 );
			genesis.setTransferHash( new byte [ ] {} );
			genesis.setUuid( Base58.decode( "o2mbxKksL8mxQnA6G4v7NvARgzV" ) );

			// mint a genesis account
			accountDao.save( genesis );

			log.info( "Just minted a genesis account, how exciting is that????" );
			log.info( genesis.toString() );

		}
	}

	public GetBalanceResponse getBalance ( GetBalanceRequest request ) throws Exception {

		long st = System.currentTimeMillis();

		final GetBalanceResponse localResponse = getLocalAccountBalance( request );

		final GetBalanceResponse remoteResponse = new GetBalanceResponse();

		// Get network view on the balance
		validationNetworkClient.getBalance( request, remoteResponse );

		long et = System.currentTimeMillis();
		log.debug( "Getting balance took: " + ( et - st ) + " ms." );

		if ( remoteResponse.getHeight() > localResponse.getHeight() ) {
			updateLocalAccountBalance( request, localResponse, remoteResponse );
			return remoteResponse;
		} else {
			return localResponse;
		}

	}

	@Transactional ( readOnly = false )
	private void updateLocalAccountBalance ( GetBalanceRequest request, GetBalanceResponse localResponse, GetBalanceResponse remoteResponse ) {

		final byte [ ] accountUuid = new ZDPAccountUuid( request.getAccountUuid() ).getPublicKeyHash();

		Account account = this.accountDao.findByUuid( accountUuid );

		if ( account == null ) {
			account = new Account();
			account.setUuid( accountUuid );
		}

		account.setBalance( new BigDecimal( remoteResponse.getAmount() ) );
		account.setHeight( remoteResponse.getHeight() );
		account.setTransferHash( remoteResponse.getChainHash() );

		this.accountDao.save( account );

	}

	@Transactional ( readOnly = true )
	public GetBalanceResponse getLocalAccountBalance ( GetBalanceRequest req ) {

		final GetBalanceResponse resp = new GetBalanceResponse();

		// Generate account from public key
		final byte [ ] accountUuid = new ZDPAccountUuid( req.getAccountUuid() ).getPublicKeyHash();

		Account account = this.accountDao.findByUuid( accountUuid );

		if ( account != null ) {
			resp.setCurve( account.getCurve() );
			resp.setAmount( account.getBalance().toPlainString() );
			resp.setHeight( account.getHeight() );
			resp.setChainHash( account.getTransferHash() );
		}

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
