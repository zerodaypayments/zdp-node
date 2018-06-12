package io.zdp.node.service.validation.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.zdp.model.network.NetworkTopologyService;
import io.zdp.node.network.validation.ValidationNetworkMQManager;
import io.zdp.node.service.validation.LockedAccountsCache;
import io.zdp.node.service.validation.model.TransferConfirmationRequest;
import io.zdp.node.service.validation.model.TransferConfirmationResponse;
import io.zdp.node.service.validation.service.TransferConfirmationService;
import io.zdp.node.service.validation.service.ValidationNodeSigner;
import io.zdp.node.storage.account.domain.Account;
import io.zdp.node.storage.account.service.AccountService;
import io.zdp.node.storage.transfer.dao.TransferHeaderDao;

@Component(value = "newTransferListener")
public class NewTransferListener {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private NetworkTopologyService networkService;

	@Autowired
	private AccountService accountService;

	@Autowired
	private TransferHeaderDao transferHeaderDao;

	@Autowired
	private LockedAccountsCache lockedAccountsCache;

	@Autowired
	private ValidationNetworkMQManager  validationNetworkMQManager;
	
	@Autowired
	private ValidationNodeSigner validationNodeSigner;
	
	@Autowired
	private TransferConfirmationService transferConfirmationService;
	

	public void process(TransferConfirmationRequest req) {

		log.debug("New transfer: " + req);
		
		if (false ==validationNodeSigner.isValidSignature(req) ) {
			
		}

		// Load account from local storage
		Account fromAccount = this.accountService.findByUuid(req.getFromAccountUuid());
		Account toAccount = this.accountService.findByUuid(req.getToAccountUuid());

		// Start transaction
		lockedAccountsCache.add(req.getFromAccountUuid());
		lockedAccountsCache.add(req.getToAccountUuid());

		TransferConfirmationResponse resp = new TransferConfirmationResponse();
		resp.setFromAccount(fromAccount);
		resp.setToAccount(toAccount);
		resp.setTransferUuid(transferUuid);

		validationNetworkMQManager.send(req.getServerUuid(),  resp);
		
	}

}
