package io.zdp.node.service.validation.service;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.zdp.node.service.validation.cache.TransferConsensusCache;
import io.zdp.node.service.validation.getAccounts.GetNodeAccountsResponse;
import io.zdp.node.service.validation.model.UnconfirmedTransfer;
import io.zdp.node.storage.account.domain.Account;

@Service
public class TransferConsensusService {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private TransferConsensusCache transferConsensusCache;

	public void settle(final UnconfirmedTransfer transfer) {

		log.debug("Settle: " + transfer);
		
		synchronized (transfer) {

			if (transferConsensusCache.contains(transfer)) {
				log.debug("Already in settlement, ignore: " + transfer);
				return;
			}

			transferConsensusCache.add(transfer);
		}

		// Group by FROM accounts
		final Map<String, Account> fromAccounts = new HashMap<>();
		
		for (GetNodeAccountsResponse accounts:transfer.getConfirmations()) {
		}


	}

}
