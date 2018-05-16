package io.zdp.node.storage.transfer.domain;

import java.io.Serializable;

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
public class TransferHeader implements Serializable {

	@Id
	@Column(name = "ID", nullable = false, updatable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "UUID", columnDefinition = "BINARY(20)", nullable = false, unique = true)
	private byte[] uuid;

	public TransferHeader() {
		super();
	}

	public TransferHeader(byte[] uuid) {
		super();
		this.uuid = uuid;
	}

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

	@Override
	public String toString() {
		return "TransferHeader [id=" + id + ", uuid=" + Hex.toHexString(uuid) + "]";
	}

}
