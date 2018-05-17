package io.zdp.node.web.api.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.zdp.node.service.validation.PrepareService;
import io.zdp.node.web.api.validation.model.ValidationPrepareTransferRequest;
import io.zdp.node.web.api.validation.model.ValidationPrepareTransferResponse;

@RestController
public class ValidationPrepareAction {

	@Autowired
	private PrepareService voteService;

	@RequestMapping(path = Urls.URL_VOTE)
	@ResponseBody
	public ValidationPrepareTransferResponse vote(@RequestBody ValidationPrepareTransferRequest req) throws Exception {
		return voteService.prepare(req);
	}
}
