package io.zdp.node.network.server.tx.vote;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;

import io.zdp.node.domain.Account;

/**
 * Prepare transfer response
 * 
 * @author sxn144
 *
 */
@SuppressWarnings("serial")
public class VoteResponse implements Serializable {

	public static enum Status {
		APPROVED, //
		APPROVED_NO_ACCOUNT_ON_FILE, //
		REJECTED_ACCOUNT_IN_PROGRESS //
	}

	private Account account;

	private Status status;

	private byte[] signedRequestUuid;

	public byte[] getSignedRequestUuid() {
		return signedRequestUuid;
	}

	public void setSignedRequestUuid(byte[] signedRequestUuid) {
		this.signedRequestUuid = signedRequestUuid;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

}
