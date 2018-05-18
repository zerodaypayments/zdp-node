package io.zdp.node.storage.account.service;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.zdp.api.model.v1.GetBalanceRequest;
import io.zdp.api.model.v1.GetBalanceResponse;
import io.zdp.crypto.Curves;
import io.zdp.crypto.account.ZDPAccountUuid;
import io.zdp.node.storage.account.dao.AccountDao;
import io.zdp.node.storage.account.domain.Account;

@Service
public class AccountService {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private AccountDao accountDao;

	@Transactional(readOnly = false)
	public GetBalanceResponse getBalance(GetBalanceRequest request) throws Exception {

		long st = System.currentTimeMillis();

		final GetBalanceResponse resp = new GetBalanceResponse();

		// Generate account from public key
		final byte[] accountUuid = new ZDPAccountUuid(request.getAccountUuid()).getPublicKeyHash();

		Account addr = this.accountDao.findByUuid(accountUuid);

		if (addr != null) {
			resp.setAmount(addr.getBalance().toPlainString());
		} else {

			// TODO remove me
			addr = new Account();
			addr.setBalance(BigDecimal.valueOf(100));
			addr.setUuid(accountUuid);
			addr.setHeight(0);
			addr.setTransferHash(new byte[] {});
			addr.setCurve(Curves.DEFAULT_CURVE_INDEX);
			this.accountDao.save(addr);

			resp.setAmount(addr.getBalance().toPlainString());
		}

		long et = System.currentTimeMillis();

		log.debug("Getting balance took: " + (et - st) + " ms.");

		return resp;
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
