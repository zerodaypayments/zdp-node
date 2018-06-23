package io.zdp.node.web.api.client;

import java.util.List;
import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.zdp.api.model.v1.GetBalanceRequest;
import io.zdp.api.model.v1.GetBalanceResponse;
import io.zdp.api.model.v1.GetNewAccountRequest;
import io.zdp.api.model.v1.GetNewAccountResponse;
import io.zdp.api.model.v1.Urls;
import io.zdp.crypto.Curves;
import io.zdp.crypto.account.ZDPAccountUuid;
import io.zdp.crypto.key.ZDPKeyPair;
import io.zdp.crypto.mnemonics.Mnemonics;
import io.zdp.crypto.mnemonics.Mnemonics.Language;
import io.zdp.node.service.validation.balance.AccountBalanceCache;
import io.zdp.node.storage.account.domain.Account;
import io.zdp.node.storage.account.service.AccountService;
import io.zdp.node.storage.account.service.GetAccountBalanceService;

@RestController
public class AccountAction {

	@Autowired
	private AccountService addressService;
	
	@Autowired
	private AccountBalanceCache accountBalanceCache;
	
	@Autowired
	private GetAccountBalanceService getAccountBalanceService;

	@RequestMapping(path = Urls.URL_GET_BALANCE)
	@ResponseBody
	public GetBalanceResponse balance(@RequestBody GetBalanceRequest request) throws Exception {
		
		final ZDPAccountUuid accountUuid = new ZDPAccountUuid(request.getAccountUuid());
		
		final GetBalanceResponse response = new GetBalanceResponse();
		
		if (accountBalanceCache.contains(accountUuid.getPublicKeyHash())) {
			
			Account account = accountBalanceCache.get(accountUuid.getPublicKeyHash());
			
			response.setAmount(account.getBalance().toPlainString());
			response.setChainHash(account.getTransferHash());
			response.setHeight(account.getHeight());
			
		} else {
		/*	
		 * TODO
			Future<GetBalanceResponse> balance = getAccountBalanceService.getBalance(request, accountUuid);
			new AsyncResult<
			*/
		}

		return response;
		
	}

	@RequestMapping(path = Urls.URL_GET_NEW_ACCOUNT)
	@ResponseBody
	public GetNewAccountResponse getNewAccount(@RequestBody(required = false) GetNewAccountRequest req) throws Exception {

		if (req == null) {
			req = new GetNewAccountRequest();
			req.setCurve(Curves.DEFAULT_CURVE);
			req.setLanguage(Language.ENGLISH);
		}

		if (false == Curves.getAvailableCurves().contains(req.getCurve())) {
			GetNewAccountResponse resp = new GetNewAccountResponse();
			resp.getResponseMetadata().setComment("Curve not found");
			return resp;
		}

		ZDPKeyPair kp = ZDPKeyPair.createRandom(req.getCurve());

		GetNewAccountResponse resp = new GetNewAccountResponse();
		resp.setCurve(req.getCurve());
		resp.setPrivateKey(kp.getPrivateKeyAsBase58());
		resp.setPublicKey(kp.getPublicKeyAsBase58());
		resp.setAccountUuid(kp.getZDPAccount().getUuid());

		List<String> words = Mnemonics.generateWords(Language.ENGLISH, kp.getPrivateKeyAsBase58());
		resp.setMnemonics(words);

		return resp;
	}

}
