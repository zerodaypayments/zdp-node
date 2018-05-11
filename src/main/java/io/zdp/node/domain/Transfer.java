package io.zdp.node.domain;

import java.io.Serializable;

public class Transfer implements Serializable {

	// 20 bytes
	private byte[] from;

	// 20 bytes
	private byte[] to;

	// 8 bytes
	private long time;

	// 8 bytes
	private long amount;

	// 2 bytes
	private short fee;

	// 64 bytes
	private String memo;

	public byte[] getFrom() {
		return from;
	}

	public void setFrom(byte[] from) {
		this.from = from;
	}

	public byte[] getTo() {
		return to;
	}

	public void setTo(byte[] to) {
		this.to = to;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public long getAmount() {
		return amount;
	}

	public void setAmount(long amount) {
		this.amount = amount;
	}

	public short getFee() {
		return fee;
	}

	public void setFee(short fee) {
		this.fee = fee;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String toRecordString() {
		return "test transfers";
	}

	public String getUuid() {
		// TODO Auto-generated method stub
		return null;
	}

}
