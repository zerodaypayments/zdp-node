package io.zdp.node.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import io.zdp.api.model.v1.TransferRequest;
import io.zdp.api.model.v1.TransferResponse;

@Service
public class TransferService {

	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	public TransferResponse transfer(TransferRequest request) {
		
		log.debug("Received new client transfer request: " + request);
		
		// Validate request
		
		// If Account in progress, reject 
		
		// Ask Validation Network on their opinion 
		// Validation nodes return FROM Account details (balance/height/transfer chain hash) 
		
		// Continue with Consensus account details 
		
		// Broadcast Commit/Rollback transfer and new account details
		
		// Broadcast transaction
			
		
		return null;
		
	}
	
	
}
