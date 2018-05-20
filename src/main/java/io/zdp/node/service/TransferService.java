package io.zdp.node.service;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.zdp.api.model.v1.TransferRequest;
import io.zdp.api.model.v1.TransferResponse;
import io.zdp.node.common.StringHelper;
import io.zdp.node.domain.ValidatedTransferRequest;
import io.zdp.node.error.TransferException;
import io.zdp.node.network.validation.ValidationNetworkClient;
import io.zdp.node.storage.account.dao.AccountDao;
import io.zdp.node.storage.account.domain.Account;
import io.zdp.node.storage.transfer.dao.CurrentTransferDao;
import io.zdp.node.storage.transfer.dao.TransferHeaderDao;
import io.zdp.node.storage.transfer.domain.CurrentTransfer;
import io.zdp.node.storage.transfer.domain.TransferHeader;

@Service
public class TransferService {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	public static final BigDecimal TX_FEE = BigDecimal.valueOf(0.0001);

	public static final String TX_PREFIX = "tx";

	@Autowired
	private AccountDao accountDao;

	@Autowired
	private CurrentTransferDao transferDao;

	@Autowired
	private TransferHeaderDao transferHeaderDao;

	@Autowired
	private TransferValidationService validationService;

	@Autowired
	private ValidationNetworkClient validationNetworkClient;

	/**
	 * Make a transfer
	 */
	public TransferResponse transfer(TransferRequest request) throws TransferException {

		log.debug("Request: " + request);

		final TransferResponse resp = new TransferResponse();

		// Validate transfer request
		final ValidatedTransferRequest enrichedTransferRequest = validationService.validate(request);

		// Ask Validation network
		boolean prepared = validationNetworkClient.prepare(enrichedTransferRequest);

		if (prepared) {

			save(enrichedTransferRequest, resp);

			validationNetworkClient.commit(enrichedTransferRequest);

		} else {

			resp.setError(TransferResponse.ERROR_REJECTED);

			validationNetworkClient.rollback(enrichedTransferRequest);

		}

		return resp;

	}

	@Transactional(readOnly = false)
	private void save(ValidatedTransferRequest req, final TransferResponse resp) {

		// Save transfer header
		TransferHeader th = new TransferHeader();
		th.setUuid(req.getTransactionSignature());
		this.transferHeaderDao.save(th);

		// Save Current Transfer
		final CurrentTransfer transfer = new CurrentTransfer();
		transfer.setUuid(req.getTransactionUuid());
		transfer.setAmount(req.getAmount().toPlainString());
		transfer.setDate(req.getTime());
		transfer.setFrom(req.getFromAccountUuid().getUuid());
		transfer.setTo(req.getToAccountUuid().getUuid());
		transfer.setMemo(StringHelper.cleanUpMemo(req.getMemo()));
		transfer.setFee(req.getFee().toPlainString());
		this.transferDao.save(transfer);

		resp.setUuid(req.getTransactionUuid());

		log.debug("Saved tx: " + transfer);

		// Update balances
		Account from = this.accountDao.findByUuid(req.getFromAccountUuid().getPublicKeyHash());
		Account to = this.accountDao.findByUuid(req.getToAccountUuid().getPublicKeyHash());

		BigDecimal newFromBalance = from.getBalance().subtract(req.getTotalAmount());
		from.setBalance(newFromBalance);

		this.accountDao.save(from);

		log.debug("saved new from balance: " + from);

		BigDecimal newToBalance = to.getBalance().add(req.getAmount());
		to.setBalance(newToBalance);
		this.accountDao.save(to);

		log.debug("saved new to balance: " + to);

		log.debug("Response: " + resp);

	}

}