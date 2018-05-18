package io.zdp.node.storage.transfer.domain;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

@SuppressWarnings("serial")
public class CurrentTransfer implements Serializable {

	private String uuid;

	private String from;

	private String to;

	private long date;

	private String amount;

	private String fee;

	private String memo = StringUtils.EMPTY;

	public String toRecordString() {

		StringBuilder sb = new StringBuilder();

		sb.append(StringUtils.rightPad(uuid, 32, StringUtils.SPACE));
		sb.append("|");
		sb.append(StringUtils.rightPad(from, 38, StringUtils.SPACE));
		sb.append("|");
		sb.append(StringUtils.rightPad(to, 38, StringUtils.SPACE));
		sb.append("|");
		sb.append(StringUtils.rightPad(Long.toString(date), 20, StringUtils.SPACE));
		sb.append("|");
		sb.append(StringUtils.rightPad(amount, 20, StringUtils.SPACE));
		sb.append("|");
		sb.append(StringUtils.rightPad(fee, 8, StringUtils.SPACE));
		sb.append("|");
		sb.append(memo);

		return sb.toString();

	}

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

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getFee() {
		return fee;
	}

	public void setFee(String fee) {
		this.fee = fee;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
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
		CurrentTransfer other = (CurrentTransfer) obj;
		if (uuid == null) {
			if (other.uuid != null)
				return false;
		} else if (!uuid.equals(other.uuid))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "CurrrentTransfer [uuid=" + uuid + ", from=" + from + ", to=" + to + ", date=" + date + ", amount=" + amount + ", fee=" + fee + ", memo=" + memo + "]";
	}

}
