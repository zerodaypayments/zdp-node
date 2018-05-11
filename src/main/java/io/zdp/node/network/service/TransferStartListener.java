package io.zdp.node.network.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Make sure transfer is in a transaction
 * 
 * @author sxn144
 *
 */
@Component
public class TransferStartListener {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private AccountsInProgressCache accountsInProgressCache;
	
	public boolean start(String accountUuid, String serverUuid) {
		
		if (accountsInProgressCache.inProgress(accountUuid)) {
			return false;
		}
		
		return true;
		
	}
	
}
