package io.zdp.node.service.validation.model;

import org.apache.commons.lang3.ArrayUtils;
import org.bouncycastle.util.encoders.Hex;

import io.zdp.crypto.Hashing;

/**
 * Prepare transfer response
 */
@SuppressWarnings("serial")
public class TransferConfirmationRequest extends NetworkBaseSignedObject {

	private byte[] transactionUuid;

	private byte[] fromAccountUuid;

	private byte[] toAccountUuid;

	public byte[] getTransactionUuid() {
		return transactionUuid;
	}

	public void setTransactionUuid(byte[] transactionUuid) {
		this.transactionUuid = transactionUuid;
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
	public byte[] toHash() {
		byte[] hash = new byte[] {};
		hash = ArrayUtils.addAll(hash, transactionUuid);
		hash = ArrayUtils.addAll(hash, fromAccountUuid);
		hash = ArrayUtils.addAll(hash, toAccountUuid);
		return Hashing.ripemd160(hash);
	}

	@Override
	public String toString() {
		return "TransferConfirmationRequest [transactionUuid=" + Hex.toHexString(transactionUuid) + ", fromAccountUuid=" + Hex.toHexString(fromAccountUuid) + ", toAccountUuid=" + Hex.toHexString(toAccountUuid) + "]";
	}

}
