package io.zdp.node.web.api.validation.model;

import io.zdp.api.model.v1.BaseResponseObject;

@SuppressWarnings("serial")
public class ValidationPingResponse extends BaseResponseObject {

	private String uuid;

	private String publicKey;

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}

	public ValidationPingResponse() {
		super();
	}

	@Override
	public String getType() {
		return "validation-ping";
	}

	@Override
	public String toString() {
		return "PingResponse [getType()=" + getType() + "]";
	}

}