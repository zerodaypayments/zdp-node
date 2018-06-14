package io.zdp.node.service.validation.settle;

import java.util.Arrays;

import io.zdp.node.service.validation.model.NetworkBaseSignedObject;
import io.zdp.node.storage.account.domain.Account;
import io.zdp.node.storage.transfer.domain.CurrentTransfer;

/**
 * Prepare transfer response
 */
@SuppressWarnings("serial")
public class TransferSettlementRequest extends NetworkBaseSignedObject {

	private Account fromAccount;

	private Account toAccount;

	private CurrentTransfer currentTransfer;

	private byte[] transactionUuid;

	public TransferSettlementRequest() {
		super();
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

	public CurrentTransfer getCurrentTransfer() {
		return currentTransfer;
	}

	public void setCurrentTransfer(CurrentTransfer currentTransfer) {
		this.currentTransfer = currentTransfer;
	}

	public byte[] getTransactionUuid() {
		return transactionUuid;
	}

	public void setTransactionUuid(byte[] transactionUuid) {
		this.transactionUuid = transactionUuid;
	}

	@Override
	public String toString() {
		return "TransferSettlementRequest [fromAccount=" + fromAccount + ", toAccount=" + toAccount + ", currentTransfer=" + currentTransfer + ", transactionUuid=" + Arrays.toString(transactionUuid) + "]";
	}

	@Override
	public byte[] toHash() {
		// TODO Auto-generated method stub
		return null;
	}

}
