package io.zdp.node.web.api.validation.model;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.zdp.crypto.Base58;
import io.zdp.crypto.Hashing;
import io.zdp.node.service.TransferService;

/**
 * Prepare transfer request
 */
@SuppressWarnings("serial")
public class ValidationPrepareTransferRequest implements Serializable {

	private static final Logger log = LoggerFactory.getLogger(ValidationPrepareTransferRequest.class);

	private String fromAccountUuid;

	private String toAccountUuid;

	private String transferUuid;

	private byte[] signedRequest;

	private String serverUuid;

	private String requestUuid = UUID.randomUUID().toString();

	public byte[] toHashData() {
		return Hashing.ripemd160((requestUuid + " " + fromAccountUuid + " " + toAccountUuid + " " + transferUuid + " " + serverUuid).getBytes(StandardCharsets.UTF_8));
	}

	public byte[] getSignedRequest() {
		return signedRequest;
	}

	public void setSignedRequest(byte[] signedRequest) {
		this.signedRequest = signedRequest;
	}

	public String getRequestUuid() {
		return requestUuid;
	}

	public void setRequestUuid(String requestUuid) {
		this.requestUuid = requestUuid;
	}

	public String getServerUuid() {
		return serverUuid;
	}

	public void setServerUuid(String serverUuid) {
		this.serverUuid = serverUuid;
	}

	public String getFromAccountUuid() {
		return fromAccountUuid;
	}

	public void setFromAccountUuid(String fromAccountUuid) {
		this.fromAccountUuid = fromAccountUuid;
	}

	public String getToAccountUuid() {
		return toAccountUuid;
	}

	public void setToAccountUuid(String toAccountUuid) {
		this.toAccountUuid = toAccountUuid;
	}

	public String getTransferUuid() {
		return transferUuid;
	}

	public byte[] getRawTransferUuid() {
		try {
			return Base58.decode(StringUtils.removeStart(TransferService.TX_PREFIX, this.transferUuid));
		} catch (Exception e) {
			log.error("Error: ", e);
		}

		return null;
	}

	public void setTransferUuid(String transferUuid) {
		this.transferUuid = transferUuid;
	}

	@Override
	public String toString() {
		return "ValidationPrepareTransferRequest [fromAccountUuid=" + fromAccountUuid + ", toAccountUuid=" + toAccountUuid + ", transferUuid=" + transferUuid + ", serverUuid=" + serverUuid + ", requestUuid=" + requestUuid + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((requestUuid == null) ? 0 : requestUuid.hashCode());
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
		ValidationPrepareTransferRequest other = (ValidationPrepareTransferRequest) obj;
		if (requestUuid == null) {
			if (other.requestUuid != null)
				return false;
		} else if (!requestUuid.equals(other.requestUuid))
			return false;
		return true;
	}

}
