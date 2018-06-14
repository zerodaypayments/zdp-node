package io.zdp.node.service.validation.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.zdp.model.network.NetworkTopologyService;
import io.zdp.node.storage.account.service.AccountService;
import io.zdp.node.storage.transfer.dao.CurrentTransferDao;
import io.zdp.node.storage.transfer.dao.TransferHeaderDao;
import io.zdp.node.storage.transfer.service.TransferHeaderService;

@Component(value = "transferConfirmationListener")
public class TransferConfirmationListener {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private NetworkTopologyService networkService;

	@Autowired
	private AccountService accountService;

	@Autowired
	private TransferHeaderDao transferHeaderDao;

	@Autowired
	private TransferHeaderService transferHeaderService;

	@Autowired
	private CurrentTransferDao currentTransferDao;
/*
	public void process(TransferSettlement req) {

		log.debug("Transfer settlement: " + req);

		// Validate request (otherwise malicious actors can start locking accounts)
		if (false == networkService.isValidServerRequest(req.getServerUuid(), req.toHashData(), req.getRequestSignature())) {
			log.error("Signature not verified");
			return;
		}

		updateAccounts(req);

		saveTransferHeader(req);

		saveCurrentTransfer(req);

	}

	private void saveCurrentTransfer(TransferSettlement req) {

		log.debug("saveCurrentTransfer: " + req.getFromAccount());

		currentTransferDao.save(req.getTransfer());

	}

	private void saveTransferHeader(TransferSettlement req) {
		transferHeaderService.save(new TransferHeader(req.getTransferSignature()));
	}

	private void updateAccounts(TransferSettlement req) {

		{
			// Update or save FROM account;
			Account from = accountService.findByUuid(req.getFromAccount().getUuidAsBytes());

			if (from == null) {
				from = new Account();
				from.setCurve(req.getFromAccount().getCurve());
				from.setUuid(req.getToAccount().getUuidAsBytes());
			}

			from.setBalance(req.getFromAccount().getBalance());
			from.setHeight(req.getFromAccount().getHeight());
			from.setTransferHash(req.getFromAccount().getTransferHash());

			log.debug("Updated FROM account: " + req.getFromAccount());

			this.accountService.save(from);

		}

		{
			// to account
			Account to = accountService.findByUuid(req.getToAccount().getUuidAsBytes());

			if (to == null) {
				to = new Account();
				to.setCurve(req.getToAccount().getCurve());
				to.setUuid(req.getToAccount().getUuidAsBytes());
			}

			to.setBalance(req.getToAccount().getBalance());
			to.setHeight(req.getToAccount().getHeight());
			to.setTransferHash(req.getToAccount().getTransferHash());

			log.debug("Updated TO account: " + req.getToAccount());

			this.accountService.save(to);
		}

	}
*/
}
