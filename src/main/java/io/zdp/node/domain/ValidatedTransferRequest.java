package io.zdp.node.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

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

	private BigDecimal amount;

	private BigDecimal fee;

	private byte[] transactionSignature;

	private String transactionUuid;

	private String memo;

	private long time;
	
	public byte[] getTransactionSignature() {
		return transactionSignature;
	}

	public void setTransactionSignature(byte[] transactionSignature) {
		this.transactionSignature = transactionSignature;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public BigDecimal getAmount() {
		return amount.setScale(8, RoundingMode.HALF_DOWN);
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

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
		return amount.add(fee).setScale(8, RoundingMode.HALF_DOWN);
	}

	public BigDecimal getFee() {
		return fee.setScale(8, RoundingMode.HALF_DOWN);
	}

	public void setFee(BigDecimal fee) {
		this.fee = fee;
	}

	@Override
	public String toString() {
		return "ValidatedTransferRequest [getTransactionUuid()=" + getTransactionUuid() + ", getRequest()=" + getRequest() + ", getFromAccountUuid()=" + getFromAccountUuid() + ", getToAccountUuid()=" + getToAccountUuid() + ", getFromAccount()=" + getFromAccount() + ", getToAccount()=" + getToAccount() + ", getTotalAmount()=" + getTotalAmount() + ", getFee()=" + getFee() + "]";
	}

}
