package io.zdp.node.web.api.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.zdp.node.service.validation.CommitService;
import io.zdp.node.service.validation.PrepareService;
import io.zdp.node.service.validation.RollbackService;
import io.zdp.node.web.api.validation.model.ValidationCommitRequest;
import io.zdp.node.web.api.validation.model.ValidationPrepareTransferRequest;
import io.zdp.node.web.api.validation.model.ValidationPrepareTransferResponse;

@RestController
public class ValidationTransferAction {

	@Autowired
	private PrepareService voteService;

	@Autowired
	private RollbackService rollbackService;

	@Autowired
	private CommitService commitService;

	@RequestMapping(path = Urls.URL_VOTE)
	@ResponseBody
	public ValidationPrepareTransferResponse vote(@RequestBody ValidationPrepareTransferRequest req) throws Exception {
		return voteService.prepare(req);
	}

	@RequestMapping(path = Urls.URL_COMMIT)
	@ResponseBody
	public Boolean commit(@RequestBody ValidationCommitRequest req) throws Exception {
		return commitService.commit(req);
	}

	@RequestMapping(path = Urls.URL_ROLLBACK)
	@ResponseBody
	public Boolean rollback(@RequestBody ValidationPrepareTransferRequest req) throws Exception {
		return rollbackService.rollback(req);
	}

}
