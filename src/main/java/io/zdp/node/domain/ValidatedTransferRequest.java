package io.zdp.node.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import io.zdp.api.model.v1.TransferRequest;
import io.zdp.crypto.account.ZDPAccountUuid;
import io.zdp.node.storage.account.domain.Account;

@SuppressWarnings("serial")
public final class ValidatedTransferRequest implements Serializable {

	private TransferRequest request;

	private ZDPAccountUuid fromAccountUuid;

	private ZDPAccountUuid toAccountUuid;

	private Account fromAccount;

	private Account toAccount;

	private BigDecimal totalAmount;

	private BigDecimal fee;

	private String transactionUuid;

	public String getTransactionUuid() {
		return transactionUuid;
	}

	public void setTransactionUuid(String transactionUuid) {
		this.transactionUuid = transactionUuid;
	}

	public TransferRequest getRequest() {
		return request;
	}

	public void setRequest(TransferRequest request) {
		this.request = request;
	}

	public ZDPAccountUuid getFromAccountUuid() {
		return fromAccountUuid;
	}

	public void setFromAccountUuid(ZDPAccountUuid fromAccountUuid) {
		this.fromAccountUuid = fromAccountUuid;
	}

	public ZDPAccountUuid getToAccountUuid() {
		return toAccountUuid;
	}

	public void setToAccountUuid(ZDPAccountUuid toAccountUuid) {
		this.toAccountUuid = toAccountUuid;
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

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public BigDecimal getFee() {
		return fee;
	}

	public void setFee(BigDecimal fee) {
		this.fee = fee;
	}

	@Override
	public String toString() {
		return "ValidatedTransferRequest [request=" + request + ", fromAccountUuid=" + fromAccountUuid + ", toAccountUuid=" + toAccountUuid + ", fromAccount=" + fromAccount + ", toAccount=" + toAccount + ", totalAmount=" + totalAmount + ", fee=" + fee + "]";
	}

}
