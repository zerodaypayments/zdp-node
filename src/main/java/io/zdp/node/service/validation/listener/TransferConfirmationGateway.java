package io.zdp.node.service.validation.listener;

import io.zdp.node.service.validation.model.TransferConfirmationResponse;

public interface TransferConfirmationGateway {

	public static final String QUEUE_TRANSFER_CONFIRMATION = "transfer-confirmation";

	void send(TransferConfirmationResponse req);

}
