package io.zdp.node.error;

@SuppressWarnings("serial")
public class TransferException extends Exception {

	private String error;

	public TransferException(String error) {
		super();
		this.error = error;
	}

	public String getError() {
		return error;
	}

	@Override
	public String toString() {
		return "TransferException [error=" + error + "]";
	}

}
