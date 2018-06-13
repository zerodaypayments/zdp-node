package io.zdp.node.service.validation.getAccounts;

import java.nio.charset.StandardCharsets;

import org.apache.commons.lang3.ArrayUtils;

import io.zdp.node.service.validation.model.NetworkBaseSignedObject;
import io.zdp.node.storage.account.domain.Account;

/**
 * Prepare transfer response
 */
@SuppressWarnings("serial")
public class GetNodeAccountsResponse extends NetworkBaseSignedObject {

	public static enum Status {
		OK, //
		ERROR, //
		ACCOUNT_LOCKED, //
		UNAUTHORIZED, //
		REPLAY_DETECTED //
	}

	private Account fromAccount;

	private Account toAccount;

	private Status status;

	private byte[] transferUuid;

	public GetNodeAccountsResponse() {
		super();
	}

	public GetNodeAccountsResponse(Status status) {
		super();
		this.status = status;
	}

	@Override
	public byte[] toHash() {

		byte[] hash = ArrayUtils.addAll(transferUuid, null);

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

	public byte[] getTransferUuid() {
		return transferUuid;
	}

	public void setTransferUuid(byte[] transferUuid) {
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
