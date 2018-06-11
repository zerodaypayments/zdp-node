package io.zdp.node.service.validation.service;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.zdp.api.model.v1.TransferRequest;
import io.zdp.node.error.TransferException;
import io.zdp.node.service.validation.TransferMemoryPool;
import io.zdp.node.service.validation.listener.NewTransferGateway;
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

	/**
	 * Make a transfer
	 */
	public UnconfirmedTransfer transfer(TransferRequest request) throws TransferException {

		log.debug("Request: " + request);

		// Validate and enrich transfer request
		final UnconfirmedTransfer newTransferRequest = validationService.validate(request);

		// Broadcast new un-confirmed transfer to Validation network 
		newTransferGateway.send(newTransferRequest);

		// Confirm  transaction locally
		final TransferConfirmationResponse confirmedTransaction = transferConfirmationService.confirm(newTransferRequest);

		// Add confirmed transfer to a local memory pool
		memoryPool.add(confirmedTransaction);

		return newTransferRequest;
	}

}