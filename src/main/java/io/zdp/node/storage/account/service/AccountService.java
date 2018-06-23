package io.zdp.node.storage.account.service;

import java.math.BigDecimal;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.zdp.crypto.Base58;
import io.zdp.crypto.Curves;
import io.zdp.node.storage.account.dao.AccountDao;
import io.zdp.node.storage.account.domain.Account;

@Service
public class AccountService {

	public static final int NEW_ACCOUNT_HEIGHT = 1;
	public static final byte[] NEW_ACCOUNT_HASH = new byte[] {};

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private AccountDao accountDao;

	@PostConstruct
	public void init() throws Exception {
		if (accountDao.count() == 0) {

			log.debug("Seems like a fresh node start!");

			Account genesis = new Account();
			genesis.setBalance(new BigDecimal(90000000000L));
			genesis.setCurve(Curves.DEFAULT_CURVE_INDEX);
			genesis.setHeight(NEW_ACCOUNT_HEIGHT);
			genesis.setTransferHash(NEW_ACCOUNT_HASH);
			genesis.setUuid(Base58.decode("3xN7bk2FxkcuCotTF4ByGN6EYiio"));

			// mint a genesis account
			accountDao.save(genesis);

			log.info("Just minted a genesis account, how exciting is that????");
			log.info(genesis.toString());

		}
	}

	@Transactional(readOnly = true)
	public long countAccounts() {
		return this.accountDao.count();
	}

	@Transactional(readOnly = true)
	public Account findByUuid(byte[] uuid) {
		return this.accountDao.findByUuid(uuid);
	}

	@Transactional(readOnly = false)
	public Account save(Account a) {
		return this.accountDao.save(a);
	}

}
