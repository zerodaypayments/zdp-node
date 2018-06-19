package io.zdp.node.service.validation.service;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.zdp.api.model.v1.TransferRequest;
import io.zdp.node.error.TransferException;
import io.zdp.node.service.validation.cache.UnconfirmedTransferMemoryPool;
import io.zdp.node.service.validation.getAccounts.GetNodeAccountsRequest;
import io.zdp.node.service.validation.getAccounts.GetNodeAccountsResponse;
import io.zdp.node.service.validation.getAccounts.GetNodeAccountsRequestTopicPublisher;
import io.zdp.node.service.validation.getAccounts.GetNodeAccountsService;
import io.zdp.node.service.validation.model.UnconfirmedTransfer;

/**
 * Process a transfer from a client, initiate Validation network confirmation
 * process
 */
@Service
public class NewTransfersService {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	public static final BigDecimal TX_FEE = BigDecimal.valueOf(0.0001);

	public static final String TX_PREFIX = "tx";

	@Autowired
	private TransferValidationService validationService;

	@Autowired
	private UnconfirmedTransferMemoryPool memoryPool;

	@Autowired
	private GetNodeAccountsService transferConfirmationService;

	@Autowired
	private ValidationNodeSigner validationNodeSigner;

	@Autowired
	private GetNodeAccountsRequestTopicPublisher getNodeAccountsRequestTopicPublisher;

	/**
	 * Make a transfer
	 */
	public UnconfirmedTransfer transfer(TransferRequest request) throws TransferException {

		log.debug("Request: " + request);

		// Validate and enrich transfer request
		final UnconfirmedTransfer unconfirmedTransfer = validationService.validate(request);

		// Broadcast new un-confirmed transfer to Validation network
		final GetNodeAccountsRequest getNodeAccountsRequest = toGetNodeAccountsRequest(unconfirmedTransfer);
		getNodeAccountsRequestTopicPublisher.send(getNodeAccountsRequest);

		// Add confirmed transfer to a local memory pool
		memoryPool.add(unconfirmedTransfer);

		// Confirm transaction locally
		final GetNodeAccountsResponse localGetNodeAccountsResponse = transferConfirmationService.process(getNodeAccountsRequest);

		unconfirmedTransfer.getConfirmations().add(localGetNodeAccountsResponse);

		return unconfirmedTransfer;
	}

	private GetNodeAccountsRequest toGetNodeAccountsRequest(UnconfirmedTransfer unconfirmedTransaction) {

		GetNodeAccountsRequest req = new GetNodeAccountsRequest();

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