package io.zdp.node.service.validation.listener;

import io.zdp.node.service.validation.model.TransferConfirmationRequest;

public interface NewTransferGateway {

	void send(TransferConfirmationRequest t);

}
