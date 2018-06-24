package io.zdp.node.web.api.client;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import io.zdp.node.service.validation.balance.BalanceRequestCache;
import io.zdp.node.storage.account.domain.Account;
import io.zdp.node.storage.account.service.GetAccountBalanceService;

@RestController
public class AccountAction {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private AccountBalanceCache accountBalanceCache;

	@Autowired
	private GetAccountBalanceService getAccountBalanceService;

	@Autowired
	private BalanceRequestCache balanceRequestCache;

	@RequestMapping(path = Urls.URL_GET_BALANCE)
	@ResponseBody
	public GetBalanceResponse balance(@RequestBody GetBalanceRequest request, HttpServletRequest req) throws Exception {

		log.debug("Get balance: " + request);

		final ZDPAccountUuid accountUuid = new ZDPAccountUuid(request.getAccountUuid());

		if (balanceRequestCache.contains(accountUuid.getPublicKeyHash())) {
			log.error("Already in process: " + request);
			return null;
		}

		final GetBalanceResponse response = new GetBalanceResponse();

		Account account = null;

		if (accountBalanceCache.contains(accountUuid.getPublicKeyHash())) {

			account = accountBalanceCache.get(accountUuid.getPublicKeyHash());

			log.debug("Got account from cache: " + account);

		} else {

			account = getAccountBalanceService.getBalance(request, accountUuid).get();

			log.debug("Got account from network: " + account);

		}

		if (account != null) {

			response.setAmount(account.getBalance().toPlainString());
			response.setChainHash(account.getTransferHash());
			response.setHeight(account.getHeight());

		}

		log.debug("Got account balance: " + response);

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
		resp.setAccountUuid(kp.getZDPAccount().getUuid() );

		List<String> words = Mnemonics.generateWords(Language.ENGLISH, kp.getPrivateKeyAsBase58());
		resp.setMnemonics(words);

		return resp;
	}

}
