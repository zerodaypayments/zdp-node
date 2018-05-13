package io.zdp.node.web;

import java.util.List;

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
import io.zdp.crypto.key.ZDPKeyPair;
import io.zdp.crypto.mnemonics.Mnemonics;
import io.zdp.crypto.mnemonics.Mnemonics.Language;
import io.zdp.node.service.AccountService;

@RestController
public class AccountAction {

	@Autowired
	private AccountService addressService;

	@RequestMapping(path = Urls.URL_GET_BALANCE)
	@ResponseBody
	public GetBalanceResponse balance(@RequestBody GetBalanceRequest request) throws Exception {
		return addressService.getBalance(request);
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
