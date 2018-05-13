package io.zdp.node.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.bouncycastle.util.encoders.Hex;

@SuppressWarnings("serial")
@Entity
@Table(name = "transfer")
public class Transfer implements Serializable {

	private static final String TX_SUFFIX = "0z";

	public static final String TX_PREFIX = "tx00";

	@Id
	@Column(name = "ID", nullable = false, updatable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "UUID", columnDefinition = "BINARY(20)", nullable = false, unique = true)
	private byte[] uuid;

	@Column(name = "DATE", columnDefinition = "DATETIME", nullable = false, updatable = false)
	private Date date;

	@Column(name = "FROM_ACCOUNT_ID", nullable = false, updatable = false)
	private long fromAccountId;

	@Column(name = "TO_ACCOUNT_ID", nullable = false, updatable = false)
	private long toAccountId;

	@Column(name = "AMOUNT", nullable = false)
	private long amount;

	@Column(name = "FEE", columnDefinition = "SMALLINT UNSIGNED", nullable = false)
	private int fee;

	@Column(name = "MEMO", nullable = true, updatable = false, length = 64)
	private String memo;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public byte[] getUuid() {
		return uuid;
	}

	public void setUuid(byte[] uuid) {
		this.uuid = uuid;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public long getFromAccountId() {
		return fromAccountId;
	}

	public void setFromAccountId(long fromAccountId) {
		this.fromAccountId = fromAccountId;
	}

	public long getToAccountId() {
		return toAccountId;
	}

	public void setToAccountId(long toAccountId) {
		this.toAccountId = toAccountId;
	}

	public BigDecimal getAmount() {
		return new BigDecimal(amount).divide(Account.BALANCE_CO);
	}

	public void setAmount(BigDecimal balance) {
		this.amount = balance.multiply(Account.BALANCE_CO).longValue();
	}

	public BigDecimal getFee() {
		return new BigDecimal(fee).divide(Account.BALANCE_CO);
	}

	public void setFee(BigDecimal f) {
		this.fee = f.multiply(Account.BALANCE_CO).intValue();
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getUuidInPublicFormat() {
		return TX_PREFIX + Hex.toHexString(uuid) + TX_SUFFIX;
	}

}
