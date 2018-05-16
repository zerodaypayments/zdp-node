package io.zdp.node.storage.account.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.bouncycastle.util.encoders.Hex;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.zdp.crypto.Base58;

@SuppressWarnings("serial")
@Entity
@Table(name = "account")
public class Account implements Serializable {

	public static final BigDecimal BALANCE_CO = new BigDecimal(100000000);

	@Id
	@Column(name = "ID", nullable = false, updatable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonIgnore
	private long id;

	@Column(name = "UUID", columnDefinition = "BINARY(20)", nullable = false, updatable = false, unique = true)
	private byte[] uuid;

	@Column(name = "BALANCE", nullable = false)
	private long balance;

	@Column(name = "BLOCK_HEIGHT", nullable = false)
	private long height;

	@Column(name = "CURVE", columnDefinition = "SMALLINT UNSIGNED", nullable = false)
	private int curve;

	@Column(name = "HASH", columnDefinition = "BINARY(20)", nullable = false)
	private byte[] transferChainHash;

	public byte[] toHashSignature() {
		return (Hex.toHexString(uuid) + " " + balance + " " + height + " " + curve + " " + Hex.toHexString(transferChainHash)).getBytes(StandardCharsets.UTF_8);
	}

	public byte[] getTransferHash() {
		return transferChainHash;
	}

	public void setTransferHash(byte[] transferHash) {
		this.transferChainHash = transferHash;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public BigDecimal getBalance() {
		return new BigDecimal(balance).divide(BALANCE_CO);
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance.multiply(BALANCE_CO).longValue();
	}

	public String getUuid() {
		return Hex.toHexString(uuid).toUpperCase();
	}

	@JsonIgnore
	public byte[] getUuidAsBytes() {
		return uuid;
	}

	public void setUuid(String value) {
		this.uuid = Hex.decode(value);
	}

	public void setUuid(byte[] value) {
		this.uuid = value;
	}

	public long getHeight() {
		return height;
	}

	public void setHeight(long height) {
		this.height = height;
	}

	public int getCurve() {
		return curve;
	}

	public void setCurve(int curve) {
		this.curve = curve;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
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
		Account other = (Account) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Account [id=" + id + ", uuid=" + Hex.toHexString(uuid) + ", balance=" + balance + " [" + getBalance().toPlainString() + "] " + ", transferHash=" + Base58.encode(transferChainHash) + ", height=" + height + "]";
	}

}
