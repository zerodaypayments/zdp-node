package io.zdp.node.web.api.validation.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class ValidationCommitResponse implements Serializable {

	private boolean success;

	public ValidationCommitResponse() {
		super();
	}

	public ValidationCommitResponse(boolean success) {
		super();
		this.success = success;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

}
