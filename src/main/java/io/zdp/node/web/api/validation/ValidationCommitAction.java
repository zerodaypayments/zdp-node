package io.zdp.node.web.api.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.zdp.node.storage.account.domain.Account;
import io.zdp.node.storage.account.service.AccountService;
import io.zdp.node.storage.transfer.dao.CurrentTransferDao;
import io.zdp.node.storage.transfer.domain.TransferHeader;
import io.zdp.node.storage.transfer.service.TransferHeaderService;
import io.zdp.node.web.api.validation.model.ValidationCommitRequest;
import io.zdp.node.web.api.validation.model.ValidationCommitResponse;

@RestController
public class ValidationCommitAction {

	@Autowired
	private AccountService accountService;
	
	@Autowired
	private TransferHeaderService transferHeaderService;

	@Autowired
	private CurrentTransferDao currentTransferDao;

	@RequestMapping(path = Urls.URL_COMMIT)
	@ResponseBody
	public ValidationCommitResponse commit(@RequestBody ValidationCommitRequest req) throws Exception {

		updateAccount(req);
		
		saveTransferHeader(req);
		
		return new ValidationCommitResponse(true);
		
		
	}

	private void saveTransferHeader(ValidationCommitRequest req) {
		transferHeaderService.save(new TransferHeader(req.getTransferSignature()));
	}

	private void updateAccount(ValidationCommitRequest req) {
		// Update or save account;
		Account a = accountService.findByUuid(req.getAccount().getUuidAsBytes());
		
		if (a == null) {
			a = new Account();
			a.setCurve(req.getAccount().getCurve());
			a.setUuid(req.getAccount().getUuidAsBytes());
		}
		
		a.setBalance(req.getAccount().getBalance());
		a.setHeight(req.getAccount().getHeight());
		a.setTransferHash(req.getAccount().getTransferHash());
		
		this.accountService.save(a);
	}
}
