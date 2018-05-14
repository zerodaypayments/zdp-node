package io.zdp.node.network.server.tx.vote;

import java.io.Serializable;
import java.util.UUID;

import org.bouncycastle.util.encoders.Hex;

import io.zdp.crypto.Hashing;

/**
 * Prepare transfer request
 * 
 * @author sxn144
 *
 */
@SuppressWarnings("serial")
public class VoteRequest implements Serializable {

	private byte[] accountUuid;

	private byte[] requestUuid = Hashing.whirlpool(UUID.randomUUID().toString());

	public byte[] getRequestUuid() {
		return requestUuid;
	}

	public void setRequestUuid(byte[] requestUuid) {
		this.requestUuid = requestUuid;
	}

	public byte[] getAccountUuid() {
		return accountUuid;
	}

	public void setAccountUuid(byte[] accountUuid) {
		this.accountUuid = accountUuid;
	}

	public String getAccountUuidAsString() {
		return Hex.toHexString(accountUuid);
	}

}
