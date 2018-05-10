package io.zdp.node.network.domain;

import java.io.Serializable;

/**
 * 	Validation nodes
 *
 */
@SuppressWarnings("serial")
public class NetworkNode implements Serializable {

	private String hostname;

	private int port;

	private String uuid;

	private String publicKey;

	private long height;

	private String blockChainHash;

	public long getHeight() {
		return height;
	}

	public void setHeight(long height) {
		this.height = height;
	}

	public String getBlockChainHash() {
		return blockChainHash;
	}

	public void setBlockChainHash(String blockChainHash) {
		this.blockChainHash = blockChainHash;
	}

	public String getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	@Override
	public String toString() {
		return "ValidationNode [hostname=" + hostname + ", port=" + port + ", uuid=" + uuid + ", publicKey=" + publicKey + "]";
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
		NetworkNode other = (NetworkNode) obj;
		if (uuid == null) {
			if (other.uuid != null)
				return false;
		} else if (!uuid.equals(other.uuid))
			return false;
		return true;
	}

}
