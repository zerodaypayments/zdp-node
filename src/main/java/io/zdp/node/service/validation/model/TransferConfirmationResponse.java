package io.zdp.node.service.validation.model;

import java.nio.charset.StandardCharsets;

import org.apache.commons.lang3.ArrayUtils;

import io.zdp.node.storage.account.domain.Account;

/**
 * Prepare transfer response
 */
@SuppressWarnings("serial")
public class TransferConfirmationResponse extends NetworkBaseSignedObject {

	public static enum Status {
		ERROR, //
		ACCOUNT_LOCKED, //
		UNAUTHORIZED, //
		REPLAY_DETECTED //
	}

	private Account fromAccount;

	private Account toAccount;

	private Status status;

	private String transferUuid;

	public TransferConfirmationResponse() {
		super();
	}

	public TransferConfirmationResponse(Status status) {
		super();
		this.status = status;
	}

	@Override
	public byte[] toHash() {

		byte[] hash = transferUuid.getBytes(StandardCharsets.UTF_8);

		if (fromAccount != null) {
			hash = ArrayUtils.addAll(hash, fromAccount.toHashSignature());
		}

		if (toAccount != null) {
			hash = ArrayUtils.addAll(hash, toAccount.toHashSignature());
		}

		if (status != null) {
			hash = ArrayUtils.addAll(hash, status.name().getBytes(StandardCharsets.UTF_8));
		}

		return hash;
	}

	public String getTransferUuid() {
		return transferUuid;
	}

	public void setTransferUuid(String transferUuid) {
		this.transferUuid = transferUuid;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Account getFromAccount() {
		return fromAccount;
	}

	public void setFromAccount(Account fromAccount) {
		this.fromAccount = fromAccount;
	}

	public Account getToAccount() {
		return toAccount;
	}

	public void setToAccount(Account toAccount) {
		this.toAccount = toAccount;
	}

	@Override
	public String toString() {
		return "TransferConfirmation [fromAccount=" + fromAccount + ", toAccount=" + toAccount + ", status=" + status + ", serverUuid=" + serverUuid + ", transferUuid=" + transferUuid + "]";
	}

}
