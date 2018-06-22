package io.zdp.node.service.validation.getAccounts;

import org.apache.commons.lang3.ArrayUtils;
import org.bouncycastle.util.encoders.Hex;

import io.zdp.crypto.Base58;
import io.zdp.crypto.Hashing;
import io.zdp.node.service.validation.model.NetworkBaseSignedObject;

/**
 * Prepare transfer response
 */
@SuppressWarnings("serial")
public class GetNodeAccountsRequest extends NetworkBaseSignedObject {

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
		return "TransferConfirmationRequest [transactionUuid=" + Base58.encode(transactionUuid) + ", fromAccountUuid=" + Base58.encode(fromAccountUuid) + ", toAccountUuid=" + Base58.encode(toAccountUuid) + "]";
	}

}
