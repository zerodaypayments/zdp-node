package io.zdp.node.service.validation.listener;

import io.zdp.node.service.validation.model.UnconfirmedTransfer;

public interface NewTransferGateway {

	void send(UnconfirmedTransfer t);

}
