package io.zdp.node.service.validation.failed;

import org.apache.commons.lang3.ArrayUtils;

import io.zdp.crypto.Base58;
import io.zdp.crypto.Hashing;
import io.zdp.node.service.validation.model.NetworkBaseSignedObject;
import io.zdp.node.storage.transfer.domain.CurrentTransfer;

@SuppressWarnings("serial")
public class FailedTransferRequest extends NetworkBaseSignedObject {

	private CurrentTransfer currentTransfer;

	private byte[] transferUuid;

	private byte[] fromAccountUuid;

	private byte[] toAccountUuid;

	@Override
	public byte[] toHash() {

		byte[] hash = Hashing.ripemd160(currentTransfer.toRecordString());

		hash = ArrayUtils.addAll(hash, transferUuid);
		hash = ArrayUtils.addAll(hash, fromAccountUuid);
		hash = ArrayUtils.addAll(hash, toAccountUuid);

		return null;
	}

	public CurrentTransfer getCurrentTransfer() {
		return currentTransfer;
	}

	public void setCurrentTransfer(CurrentTransfer currentTransfer) {
		this.currentTransfer = currentTransfer;
	}

	public byte[] getTransferUuid() {
		return transferUuid;
	}

	public void setTransferUuid(byte[] transferUuid) {
		this.transferUuid = transferUuid;
	}

	public byte[] getFromAccountUuid() {
		return fromAccountUuid;
	}

	public void setFromAccountUuid(byte[] fromAccountUuid) {
		this.fromAccountUuid = fromAccountUuid;
	}

	public byte[] getToAccountUuid() {
		return toAccountUuid;
	}

	public void setToAccountUuid(byte[] toAccountUuid) {
		this.toAccountUuid = toAccountUuid;
	}

	@Override
	public String toString() {
		return "FailedTransferRequest [currentTransfer=" + currentTransfer + ", transferUuid=" + Base58.encode(transferUuid) + ", fromAccountUuid=" + Base58.encode(fromAccountUuid) + ", toAccountUuid=" + Base58.encode(toAccountUuid) + "]";
	}

}
