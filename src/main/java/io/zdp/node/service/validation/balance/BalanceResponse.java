package io.zdp.node.service.validation.balance;

import org.apache.commons.lang3.ArrayUtils;

import io.zdp.crypto.Hashing;
import io.zdp.node.service.validation.model.NetworkBaseSignedObject;
import io.zdp.node.storage.account.domain.Account;

@SuppressWarnings("serial")
public class BalanceResponse extends NetworkBaseSignedObject {

	private Account account;

	private byte[] accountUuid;

	public BalanceResponse(Account a) {
		this.account = a;
		this.accountUuid = a.getUuidAsBytes();
	}

	public BalanceResponse(byte[] accountUuid) {
		super();
		this.accountUuid = accountUuid;
	}

	public BalanceResponse() {
		super();
	}

	@Override
	public byte[] toHash() {

		byte[] hash = new byte[] {};

		if (account != null) {
			hash = ArrayUtils.addAll(hash, account.toHashSignature());
		}

		hash = ArrayUtils.addAll(hash, accountUuid);

		hash = Hashing.ripemd160(hash);

		return hash;
	}

	public byte[] getAccountUuid() {
		return accountUuid;
	}

	public void setAccountUuid(byte[] accountUuid) {
		this.accountUuid = accountUuid;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	@Override
	public String toString() {
		return "GetBalanceResponse [account=" + account + "]";
	}

}
