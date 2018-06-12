package io.zdp.node.service.validation.service;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.zdp.api.model.v1.TransferRequest;
import io.zdp.node.error.TransferException;
import io.zdp.node.service.validation.LockedAccountsCache;
import io.zdp.node.service.validation.TransferMemoryPool;
import io.zdp.node.service.validation.listener.NewTransferGateway;
import io.zdp.node.service.validation.model.TransferConfirmationRequest;
import io.zdp.node.service.validation.model.TransferConfirmationResponse;
import io.zdp.node.service.validation.model.UnconfirmedTransfer;

@Service
public class TransferService {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	public static final BigDecimal TX_FEE = BigDecimal.valueOf(0.0001);

	public static final String TX_PREFIX = "tx";

	@Autowired
	private TransferValidationService validationService;

	@Autowired
	private NewTransferGateway newTransferGateway;

	@Autowired
	private TransferMemoryPool memoryPool;

	@Autowired
	private TransferConfirmationService transferConfirmationService;

	@Autowired
	private ValidationNodeSigner validationNodeSigner;

	@Autowired
	private LockedAccountsCache lockedAccountsCache;
	
	/**
	 * Make a transfer
	 */
	public UnconfirmedTransfer transfer(TransferRequest request) throws TransferException {

		log.debug("Request: " + request);

		// Validate and enrich transfer request
		final UnconfirmedTransfer unconfirmedTransaction = validationService.validate(request);

		// Broadcast new un-confirmed transfer to Validation network
		final TransferConfirmationRequest transferConfirmationRequest = toTransferConfirmationRequest(unconfirmedTransaction);
		newTransferGateway.send(transferConfirmationRequest);

		// Add confirmed transfer to a local memory pool
		memoryPool.add(unconfirmedTransaction);

		// Confirm transaction locally
		final TransferConfirmationResponse confirmedTransaction = transferConfirmationService.confirm(transferConfirmationRequest);

		unconfirmedTransaction.getConfirmations().add(confirmedTransaction);
		
		return unconfirmedTransaction;
	}

	private TransferConfirmationRequest toTransferConfirmationRequest(UnconfirmedTransfer unconfirmedTransaction) {

		TransferConfirmationRequest req = new TransferConfirmationRequest();

		req.setFromAccountUuid(unconfirmedTransaction.getFromAccountUuid().getPublicKeyHash());
		req.setToAccountUuid(unconfirmedTransaction.getToAccountUuid().getPublicKeyHash());
		req.setTransactionUuid(unconfirmedTransaction.getTransactionSignature());

		try {
			validationNodeSigner.sign(req);
		} catch (Exception e) {
			log.error("Error: ", e);
		}

		return req;
	}

}