package io.zdp.node.web.api.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.zdp.api.model.v1.GetBalanceRequest;
import io.zdp.api.model.v1.GetBalanceResponse;
import io.zdp.node.storage.account.service.AccountService;

@RestController
public class ValidationBalanceAction {

	@Autowired
	private AccountService addressService;

	@RequestMapping ( path = Urls.URL_BALANCE )
	@ResponseBody
	public GetBalanceResponse balance ( @RequestBody GetBalanceRequest request ) throws Exception {
		return addressService.getLocalAccountBalance( request );
	}

}
