package io.zdp.node.service.validation.failed;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.zdp.node.service.validation.cache.LockedAccountsCache;
import io.zdp.node.service.validation.cache.RecentTransfersCache;
import io.zdp.node.storage.transfer.dao.CurrentTransferDao;

@Service
public class FailedTransferService {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private RecentTransfersCache recentTransfersCache;

	@Autowired
	private CurrentTransferDao currentTransferDao;

	@Autowired
	private LockedAccountsCache lockedAccountsCache;

	public void fail(FailedTransferRequest req) {

		log.debug("Transfer failed: " + req);

		// Save ledger record
		currentTransferDao.save(req.getCurrentTransfer());

		// Add to the recent transfer cache (for replay detection)
		recentTransfersCache.add(req.getTransferUuid());

		// Un-lock accounts
		lockedAccountsCache.remove(req.getFromAccountUuid());
		lockedAccountsCache.remove(req.getToAccountUuid());

	}
}
