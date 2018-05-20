package io.zdp.node.web.api.validation.model;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import org.bouncycastle.util.encoders.Hex;

import io.zdp.crypto.Hashing;
import io.zdp.node.storage.account.domain.Account;
import io.zdp.node.storage.transfer.domain.CurrentTransfer;

@SuppressWarnings("serial")
public class ValidationCommitRequest implements Serializable {

	private String requestUuid = UUID.randomUUID().toString();

	private byte[] requestSignature;

	private String serverUuid;

	private byte[] transferSignature;

	private CurrentTransfer transfer;

	private Account fromAccount;

	private Account toAccount;

	public byte[] toHashData() {
		return Hashing.ripemd160((requestUuid + " " + transfer.getUuid() + " " + fromAccount.getUuid() + " " + toAccount.getUuid() + " " + serverUuid).getBytes(StandardCharsets.UTF_8));
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

	public byte[] getTransferSignature() {
		return transferSignature;
	}

	public void setTransferSignature(byte[] transferSignature) {
		this.transferSignature = transferSignature;
	}

	public String getRequestUuid() {
		return requestUuid;
	}

	public void setRequestUuid(String requestUuid) {
		this.requestUuid = requestUuid;
	}

	public byte[] getRequestSignature() {
		return requestSignature;
	}

	public void setRequestSignature(byte[] requestSignature) {
		this.requestSignature = requestSignature;
	}

	public String getServerUuid() {
		return serverUuid;
	}

	public void setServerUuid(String serverUuid) {
		this.serverUuid = serverUuid;
	}

	public CurrentTransfer getTransfer() {
		return transfer;
	}

	public void setTransfer(CurrentTransfer transfer) {
		this.transfer = transfer;
	}

	@Override
	public String toString() {
		return "ValidationCommitRequest [requestUuid=" + requestUuid + ", serverUuid=" + serverUuid + ", transferSignature=" + Hex.toHexString(transferSignature) + ", transfer=" + transfer + ", fromAccount=" + fromAccount + ", toAccount=" + toAccount + "]";
	}

}
