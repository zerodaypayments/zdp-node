package io.zdp.node.web.api.validation.model;

import io.zdp.api.model.v1.BaseResponseObject;
import io.zdp.node.storage.account.domain.Account;

/**
 * Prepare transfer response
 */
@SuppressWarnings ( "serial" )
public class ValidationPrepareTransferResponse extends BaseResponseObject {

	public static enum Status {
		APPROVED, //
		ACCOUNT_LOCKED, //
		UNAUTHORIZED, //
		REPLAY_DETECTED, //
		REJECTED //
	}

	private Account fromAccount;

	private Account toAccount;

	private Status status;

	public ValidationPrepareTransferResponse ( ) {
		super();
	}

	public ValidationPrepareTransferResponse ( Status status ) {
		super();
		this.status = status;
	}

	public Status getStatus ( ) {
		return status;
	}

	public void setStatus ( Status status ) {
		this.status = status;
	}

	public Account getFromAccount ( ) {
		return fromAccount;
	}

	public void setFromAccount ( Account fromAccount ) {
		this.fromAccount = fromAccount;
	}

	public Account getToAccount ( ) {
		return toAccount;
	}

	public void setToAccount ( Account toAccount ) {
		this.toAccount = toAccount;
	}

	@Override
	public String getType ( ) {
		return "vote-response";
	}

	@Override
	public String toString ( ) {
		return "ValidationPrepareTransferResponse [fromAccount=" + fromAccount + ", toAccount=" + toAccount + ", status=" + status + "]";
	}

}
