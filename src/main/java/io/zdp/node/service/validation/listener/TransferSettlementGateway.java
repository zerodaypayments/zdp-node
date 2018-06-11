package io.zdp.node.service.validation.listener;

import io.zdp.node.service.validation.model.TransferSettlement;

public interface TransferSettlementGateway {

	void send(TransferSettlement req);

}
