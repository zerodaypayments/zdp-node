package io.zdp.node.web.api.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.zdp.api.model.v1.VoteRequest;
import io.zdp.api.model.v1.VoteResponse;
import io.zdp.node.service.validation.VoteService;

@RestController
public class ValidationVoteAction {

	@Autowired
	private VoteService voteService;

	@RequestMapping(path = Urls.URL_VOTE)
	@ResponseBody
	public VoteResponse vote(@RequestBody VoteRequest req) throws Exception {
		return voteService.prepare(req);
	}
}
