package io.zdp.node.service.validation.balance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.zdp.crypto.Base58;
import io.zdp.crypto.Hashing;
import io.zdp.node.service.validation.model.NetworkBaseSignedObject;

@SuppressWarnings("serial")
public class BalanceRequest extends NetworkBaseSignedObject {

	private byte[] accountUuid;

	private transient List<BalanceResponse> responses = Collections.synchronizedList(new ArrayList<>());

	@Override
	public byte[] toHash() {

		byte[] hash = Hashing.ripemd160(accountUuid);

		return hash;
	}

	public byte[] getAccountUuid() {
		return accountUuid;
	}

	public void setAccountUuid(byte[] accountUuid) {
		this.accountUuid = accountUuid;
	}

	public List<BalanceResponse> getResponses() {
		return responses;
	}

	@Override
	public String toString() {
		return "GetBalanceRequest [accountUuid=" + Base58.encode(accountUuid) + "]";
	}

}
