package io.zdp.node.service;

import java.math.BigDecimal;
import java.util.Date;

import org.bouncycastle.util.encoders.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.zdp.api.model.v1.TransferRequest;
import io.zdp.api.model.v1.TransferResponse;
import io.zdp.crypto.Base58;
import io.zdp.crypto.account.ZDPAccountUuid;
import io.zdp.node.common.StringHelper;
import io.zdp.node.dao.jpa.AccountDao;
import io.zdp.node.dao.jpa.TransferDao;
import io.zdp.node.domain.Account;
import io.zdp.node.domain.Transfer;
import io.zdp.node.error.TransferException;

@Service
public class TransferService {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	public static final BigDecimal TX_FEE = BigDecimal.valueOf(0.0001);

	@Autowired
	private AccountDao accountDao;

	@Autowired
	private TransferDao transferDao;

	@Autowired
	private TransferValidationService validationService;

	/**
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@Transactional(readOnly = false)
	public TransferResponse transfer(TransferRequest request) throws TransferException {

		log.debug("Request: " + request);

		final TransferResponse resp = new TransferResponse();

		// Validate transfer request
		validationService.validate(request);
		
		// Ask Validation network
		

		save(request, resp);

		return resp;

	}

	private void save(TransferRequest request, final TransferResponse resp) {
		final ZDPAccountUuid fromAccountUuid = new ZDPAccountUuid(request.getFrom());
		final Account fromAccount = this.accountDao.findByUuid(fromAccountUuid.getPublicKeyHash());

		final ZDPAccountUuid toAccountUuid = new ZDPAccountUuid(request.getTo());

		Account toAccount = this.accountDao.findByUuid(toAccountUuid.getPublicKeyHash());

		if (toAccount == null) {

			log.warn("Not found TO account: " + request.getTo());

			toAccount = new Account();
			toAccount.setBalance(BigDecimal.ZERO);
			toAccount.setCurve(toAccountUuid.getCurveAsIndex());
			toAccount.setUuid(toAccountUuid.getPublicKeyHash());

			this.accountDao.save(toAccount);

		}

		final BigDecimal totalAmount = request.getAmountAsBigDecimal().add(TX_FEE);

		byte[] signature = request.getTransferUuid();

		log.debug("txUuid: " + Hex.toHexString(signature));

		// Save Transfer
		final Transfer transfer = new Transfer();
		transfer.setDate(new Date());
		transfer.setFromAccountId(fromAccount.getId());
		transfer.setToAccountId(toAccount.getId());
		transfer.setUuid(signature);
		transfer.setAmount(request.getAmountAsBigDecimal());
		transfer.setFee(TX_FEE);
		transfer.setMemo(StringHelper.cleanUpMemo(request.getMemo()));
		this.transferDao.save(transfer);

		resp.setUuid(Transfer.TX_PREFIX + Base58.encode(transfer.getUuid()));

		log.debug("Saved tx: " + transfer);

		// Update balances
		BigDecimal newFromBalance = fromAccount.getBalance().subtract(totalAmount);
		fromAccount.setBalance(newFromBalance);
		this.accountDao.save(fromAccount);

		log.debug("saved: " + fromAccount);

		BigDecimal newToBalance = toAccount.getBalance().add(request.getAmountAsBigDecimal());
		toAccount.setBalance(newToBalance);
		this.accountDao.save(toAccount);

		log.debug("saved: " + toAccount);

		log.debug("Response: " + resp);
	}

}