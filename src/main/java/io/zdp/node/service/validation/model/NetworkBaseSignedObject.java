package io.zdp.node.service.validation.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public abstract class NetworkBaseSignedObject implements Serializable {

	protected byte[] serverSignature;

	protected String serverUuid;

	public abstract byte[] toHash();

	public byte[] getServerSignature() {
		return serverSignature;
	}

	public void setServerSignature(byte[] serverSignature) {
		this.serverSignature = serverSignature;
	}

	public String getServerUuid() {
		return serverUuid;
	}

	public void setServerUuid(String serverUuid) {
		this.serverUuid = serverUuid;
	}

}
