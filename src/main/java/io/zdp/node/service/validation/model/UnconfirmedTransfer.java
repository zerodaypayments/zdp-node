package io.zdp.node.service.validation.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.zdp.crypto.account.ZDPAccountUuid;
import io.zdp.node.service.validation.getAccounts.GetNodeAccountsResponse;

@SuppressWarnings("serial")
public final class UnconfirmedTransfer implements Serializable {

	private ZDPAccountUuid fromAccountUuid;

	private ZDPAccountUuid toAccountUuid;

	private BigDecimal amount;

	private BigDecimal fee;

	private byte[] transactionSignature;

	private String transactionUuid;

	private String memo;

	private long time = System.currentTimeMillis();

	private boolean readyToSettle;

	private List<GetNodeAccountsResponse> confirmations = new ArrayList<>();

	public boolean isReadyToSettle() {
		return readyToSettle;
	}

	public void setReadyToSettle(boolean readyToSettle) {
		this.readyToSettle = readyToSettle;
	}

	public List<GetNodeAccountsResponse> getConfirmations() {
		return confirmations;
	}

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
		return "UnconfirmedTransfer [fromAccountUuid=" + fromAccountUuid + ", toAccountUuid=" + toAccountUuid + ", amount=" + amount + ", fee=" + fee + ", transactionSignature=" + Arrays.toString(transactionSignature) + ", transactionUuid=" + transactionUuid + ", memo=" + memo + ", time=" + time + ", readyToSettle=" + readyToSettle + ", confirmations="
				+ confirmations + "]";
	}
}
