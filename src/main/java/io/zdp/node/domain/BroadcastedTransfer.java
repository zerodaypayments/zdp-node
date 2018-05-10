package io.zdp.node.domain;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.util.encoders.Hex;

/**
 * Confirmed transfer
 * 
 * @author sxn144
 *
 */
public class BroadcastedTransfer implements Serializable {

	private String uuid;

	private String from;

	private String to;

	private long date;

	private long amount;

	private int fee;

	private String memo = StringUtils.EMPTY;

	private String nodeUuid;

	private byte[] transferSignature;

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public long getDate() {
		return date;
	}

	public void setDate(long date) {
		this.date = date;
	}

	public long getAmount() {
		return amount;
	}

	public void setAmount(long amount) {
		this.amount = amount;
	}

	public int getFee() {
		return fee;
	}

	public void setFee(int fee) {
		this.fee = fee;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getNodeUuid() {
		return nodeUuid;
	}

	public void setNodeUuid(String nodeUuid) {
		this.nodeUuid = nodeUuid;
	}

	public byte[] getTransferSignature() {
		return transferSignature;
	}

	public void setTransferSignature(byte[] transferSignature) {
		this.transferSignature = transferSignature;
	}

	@Override
	public String toString() {
		return "BroadcastedTransfer [uuid=" + uuid + ", from=" + from + ", to=" + to + ", date=" + date + ", amount=" + amount + ", fee=" + fee + ", memo=" + memo + ", nodeUuid=" + nodeUuid + ", transferSignature=" + Hex.toHexString(transferSignature) + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
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
		BroadcastedTransfer other = (BroadcastedTransfer) obj;
		if (uuid == null) {
			if (other.uuid != null)
				return false;
		} else if (!uuid.equals(other.uuid))
			return false;
		return true;
	}

}
