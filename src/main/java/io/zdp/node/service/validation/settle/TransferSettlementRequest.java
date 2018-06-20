package io.zdp.node.service.validation.settle;

import org.apache.commons.lang3.ArrayUtils;

import io.zdp.crypto.Hashing;
import io.zdp.node.service.validation.model.NetworkBaseSignedObject;
import io.zdp.node.storage.account.domain.Account;
import io.zdp.node.storage.transfer.domain.CurrentTransfer;

@SuppressWarnings("serial")
public class TransferSettlementRequest extends NetworkBaseSignedObject {

	private Account fromAccount;

	private Account toAccount;

	private CurrentTransfer currentTransfer;

	private byte[] transferUuid;

	@Override
	public byte[] toHash() {
		byte[] hash = ArrayUtils.addAll(fromAccount.toHashSignature(), null);
		hash = ArrayUtils.addAll(hash, toAccount.toHashSignature());
		hash = ArrayUtils.addAll(hash, Hashing.ripemd160(currentTransfer.toRecordString()));
		hash = ArrayUtils.addAll(hash, transferUuid);

		return hash;
	}

	public byte[] getTransferUuid() {
		return transferUuid;
	}

	public void setTransferUuid(byte[] transferUuid) {
		this.transferUuid = transferUuid;
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

	@Override
	public String toString() {
		return "TransferSettlementRequest [fromAccount=" + fromAccount + ", toAccount=" + toAccount + ", currentTransfer=" + currentTransfer + "]";
	}

}
