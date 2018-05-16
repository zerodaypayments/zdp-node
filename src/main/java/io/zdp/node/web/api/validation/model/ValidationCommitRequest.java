package io.zdp.node.web.api.validation.model;

import java.io.Serializable;

import io.zdp.node.storage.account.domain.Account;
import io.zdp.node.storage.transfer.domain.CurrentTransfer;

@SuppressWarnings("serial")
public class ValidationCommitRequest implements Serializable {

	private String requestUuid;

	private byte[] requestSignature;

	private String serverUuid;

	private byte[] transferSignature;

	private CurrentTransfer transfer;

	private Account account;

	public byte[] getTransferSignature() {
		return transferSignature;
	}

	public void setTransferSignature(byte[] transferSignature) {
		this.transferSignature = transferSignature;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
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

}
