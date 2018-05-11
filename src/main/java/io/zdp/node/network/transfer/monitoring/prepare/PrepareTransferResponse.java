package io.zdp.node.network.transfer.monitoring.prepare;

import java.io.Serializable;

import org.bouncycastle.util.encoders.Hex;

/**
 * Prepare transfer response
 * 
 * @author sxn144
 *
 */
@SuppressWarnings("serial")
public class PrepareTransferResponse implements Serializable {

	public static enum Status {
		APPROVED, //
		REJECTED_NOT_VALID, //
		REJECTED_ACCOUNT_IN_PROGRESS //
	}

	private String responseUuid;

	private String transferUuid;

	private Status status;

	private String serverUuid;

	private byte[] serverSignature;

	public String getResponseUuid() {
		return responseUuid;
	}

	public void setResponseUuid(String responseUuid) {
		this.responseUuid = responseUuid;
	}

	public String getServerUuid() {
		return serverUuid;
	}

	public void setServerUuid(String serverUuid) {
		this.serverUuid = serverUuid;
	}

	public byte[] getServerSignature() {
		return serverSignature;
	}

	public void setServerSignature(byte[] serverSignature) {
		this.serverSignature = serverSignature;
	}

	public String getTransferUuid() {
		return transferUuid;
	}

	public void setTransferUuid(String transferUuid) {
		this.transferUuid = transferUuid;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "PrepareTransferResponse [responseUuid=" + responseUuid + ", transferUuid=" + transferUuid + ", status=" + status + ", serverUuid=" + serverUuid + ", serverSignature=" + Hex.toHexString(serverSignature) + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((responseUuid == null) ? 0 : responseUuid.hashCode());
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
		PrepareTransferResponse other = (PrepareTransferResponse) obj;
		if (responseUuid == null) {
			if (other.responseUuid != null)
				return false;
		} else if (!responseUuid.equals(other.responseUuid))
			return false;
		return true;
	}

}
