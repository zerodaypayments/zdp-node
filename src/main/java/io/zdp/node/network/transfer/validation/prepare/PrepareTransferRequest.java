package io.zdp.node.network.transfer.validation.prepare;

import java.io.Serializable;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.util.encoders.Hex;

/**
 * Prepare transfer request
 * 
 * @author sxn144
 *
 */
@SuppressWarnings("serial")
public class PrepareTransferRequest implements Serializable {

	private String requestUuid = UUID.randomUUID().toString();

	private String transferUuid;

	private String from;

	private byte[] fromUuid;

	private BigDecimal balance;

	private long height;

	private String hash;

	private long date;

	private String serverUuid;

	private byte[] serverSignature;

	public byte[] toSignature() {
		return (requestUuid + transferUuid + from + Hex.toHexString(fromUuid) + height + hash + date + serverUuid).getBytes(StandardCharsets.UTF_8);
	}

	public byte[] getFromUuid() {
		return fromUuid;
	}

	public void setFromUuid(byte[] fromUuid) {
		this.fromUuid = fromUuid;
	}

	public String getRequestUuid() {
		return requestUuid;
	}

	public void setRequestUuid(String requestUuid) {
		this.requestUuid = requestUuid;
	}

	public long getDate() {
		return date;
	}

	public void setDate(long date) {
		this.date = date;
	}

	public String getServerUuid() {
		return serverUuid;
	}

	public void setServerUuid(String serverUuid) {
		this.serverUuid = serverUuid;
	}

	public byte[] getServerSignature() {
		return serverSignature;
	}

	public void setServerSignature(byte[] serverSignature) {
		this.serverSignature = serverSignature;
	}

	public String getTransferUuid() {
		return transferUuid;
	}

	public void setTransferUuid(String transferUuid) {
		this.transferUuid = transferUuid;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public long getHeight() {
		return height;
	}

	public void setHeight(long height) {
		this.height = height;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((transferUuid == null) ? 0 : transferUuid.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PrepareTransferRequest other = (PrepareTransferRequest) obj;
		if (transferUuid == null) {
			if (other.transferUuid != null)
				return false;
		} else if (!transferUuid.equals(other.transferUuid))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "PrepareTransferRequest [transferUuid=" + transferUuid + ", from=" + from + ", balance=" + balance + ", height=" + height + ", hash=" + hash + ", date=" + date + ", serverUuid=" + serverUuid + ", serverSignature=" + Hex.toHexString(serverSignature) + "]";
	}

}
