package io.zdp.node.service.validation.cache.key;

import java.io.Serializable;
import java.util.Arrays;

import io.zdp.crypto.Base58;

@SuppressWarnings("serial")
public class ByteWrapper implements Serializable {

	private byte[] data;

	public ByteWrapper(byte[] data) {
		super();
		this.data = data;
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(data);
	}

	public byte[] getData() {
		return data;
	}

	@Override
	public boolean equals(Object obj) {
		return Arrays.equals(data, ((ByteWrapper) obj).getData());
	}

	@Override
	public String toString() {
		return "ByteWrapper [" + Base58.encode(data) + "]";
	}
	
	

}