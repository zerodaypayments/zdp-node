package io.zdp.node.service.validation.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import io.zdp.node.service.validation.model.UnconfirmedTransfer;

@Service
public class TransferSettlementService {

	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	public void settle(UnconfirmedTransfer transfer) {
		
		log.debug("Settle: " + transfer);
		
	}

}
