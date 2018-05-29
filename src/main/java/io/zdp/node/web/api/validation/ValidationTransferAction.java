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

	// Statistics
	private static ValidationPrepareTransferRequest lastValidationPrepareTransferRequest;
	private static ValidationPrepareTransferResponse lastValidationPrepareTransferResponse;

	private static ValidationCommitRequest lastValidationCommitRequest;
	private static boolean lastValidationCommitResponse;

	private static ValidationPrepareTransferRequest lastRollbackValidationPrepareTransferRequest;
	private static boolean lastRollbackValidationPrepareTransferResponse;

	@RequestMapping ( path = Urls.URL_VOTE )
	@ResponseBody
	public ValidationPrepareTransferResponse vote ( @RequestBody ValidationPrepareTransferRequest req ) throws Exception {

		lastValidationPrepareTransferRequest = req;

		ValidationPrepareTransferResponse resp = voteService.prepare( req );

		lastValidationPrepareTransferResponse = resp;

		return resp;
	}

	@RequestMapping ( path = Urls.URL_COMMIT )
	@ResponseBody
	public Boolean commit ( @RequestBody ValidationCommitRequest req ) throws Exception {

		lastValidationCommitRequest = req;

		boolean resp = commitService.commit( req );

		lastValidationCommitResponse = resp;

		return resp;
	}

	@RequestMapping ( path = Urls.URL_ROLLBACK )
	@ResponseBody
	public Boolean rollback ( @RequestBody ValidationPrepareTransferRequest req ) throws Exception {

		lastRollbackValidationPrepareTransferRequest = req;

		boolean resp = rollbackService.rollback( req );

		lastRollbackValidationPrepareTransferResponse = resp;

		return resp;
	}

	public static ValidationPrepareTransferRequest getLastValidationPrepareTransferRequest ( ) {
		return lastValidationPrepareTransferRequest;
	}

	public static ValidationPrepareTransferResponse getLastValidationPrepareTransferResponse ( ) {
		return lastValidationPrepareTransferResponse;
	}

	public static ValidationCommitRequest getLastValidationCommitRequest ( ) {
		return lastValidationCommitRequest;
	}

	public static boolean isLastValidationCommitResponse ( ) {
		return lastValidationCommitResponse;
	}

	public static ValidationPrepareTransferRequest getLastRollbackValidationPrepareTransferRequest ( ) {
		return lastRollbackValidationPrepareTransferRequest;
	}

	public static boolean isLastRollbackValidationPrepareTransferResponse ( ) {
		return lastRollbackValidationPrepareTransferResponse;
	}

}
