package io.zdp.node.service.validation.service;

import java.math.BigDecimal;
import java.security.PublicKey;
import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.zdp.api.model.v1.TransferRequest;
import io.zdp.api.model.v1.TransferResponse;
import io.zdp.crypto.Base58;
import io.zdp.crypto.Hashing;
import io.zdp.crypto.Keys;
import io.zdp.crypto.Signing;
import io.zdp.crypto.account.ZDPAccountUuid;
import io.zdp.node.common.StringHelper;
import io.zdp.node.error.TransferException;
import io.zdp.node.service.validation.LockedAccountsCache;
import io.zdp.node.service.validation.model.UnconfirmedTransfer;

/**
 * Performs initial validation of a new transfer
 * 
 * @author sn_1970@yahoo.com
 *
 */
@Service
public class TransferValidationService {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private LockedAccountsCache lockedAccountsCache;

	public UnconfirmedTransfer validate(final TransferRequest request) throws TransferException {

		log.debug("Request: " + request);

		UnconfirmedTransfer c = new UnconfirmedTransfer();

		try {

			// Validate addresses
			if (false == ZDPAccountUuid.isValidUuid(request.getFrom())) {
				log.error("Not valid FROM address: " + request.getFrom());
				throw new TransferException(TransferResponse.ERROR_INVALID_FROM_ACCOUNT);
			}

			if (false == ZDPAccountUuid.isValidUuid(request.getTo())) {
				log.error("Not valid TO address: " + request.getTo());
				throw new TransferException(TransferResponse.ERROR_INVALID_TO_ACCOUNT);
			}

			// Should contain only digits and one optional dot
			if (false == StringUtils.containsOnly(request.getAmount(), "0123456789.")) {
				log.error("Amount is not valid: " + request.getAmount());
				throw new TransferException(TransferResponse.ERROR_INVALID_AMOUNT);
			}

			// Check amount
			if (false == NumberUtils.isCreatable(request.getAmount())) {
				log.error("Amount is not valid: " + request.getAmount());
				throw new TransferException(TransferResponse.ERROR_INVALID_AMOUNT);
			}

			// Validate amount (must be > 0)
			if (request.getAmountAsBigDecimal().compareTo(BigDecimal.ZERO) <= 0) {
				log.error("Amount is not valid: " + request.getAmountAsBigDecimal());
				throw new TransferException(TransferResponse.ERROR_INVALID_AMOUNT);
			}

			// Extract FROM address
			final ZDPAccountUuid fromAccountUuid = new ZDPAccountUuid(request.getFrom());

			if (false == Arrays.equals(fromAccountUuid.getPublicKeyHash(), Hashing.hashPublicKey(Base58.decode(request.getPublicKey())))) {
				log.error("Don't think the tx signer is authorized to act on this FROM account");
				throw new TransferException(TransferResponse.ERROR_TX_SIGNATURE_UNAUTHORIZED);
			}

			final ZDPAccountUuid toAccountUuid = new ZDPAccountUuid(request.getTo());

			// If locked, stop
			if (lockedAccountsCache.inProgress(fromAccountUuid.getPublicKeyHash())) {
				log.error("Locked FROM account");
				throw new TransferException(TransferResponse.ERROR_LOCKED_FROM_ACCOUNT);
			}

			if (lockedAccountsCache.inProgress(toAccountUuid.getPublicKeyHash())) {
				log.error("Locked TO account");
				throw new TransferException(TransferResponse.ERROR_LOCKED_TO_ACCOUNT);
			}

			// Validate signature
			final String pubKeyCurve = fromAccountUuid.getCurve();

			final PublicKey pubKey = Keys.toPublicKey(Base58.decode(request.getPublicKey()), pubKeyCurve);

			byte[] signature = request.getUniqueTransactionUuid();

			final boolean validSignature = Signing.isValidSignature(pubKey, signature, request.getSignature());

			log.debug("validSignature: " + validSignature);

			if (validSignature) {

				c.setAmount(request.getAmountAsBigDecimal());
				c.setFee(TransferService.TX_FEE);
				c.setFromAccountUuid(fromAccountUuid);
				c.setMemo(StringHelper.cleanUpMemo(request.getMemo()));
				c.setToAccountUuid(toAccountUuid);
				c.setTransactionSignature(signature);
				c.setTransactionUuid("tx" + Base58.encode(signature) + "z");

			} else {

				log.error("Signature not verified");

				throw new TransferException(TransferResponse.ERROR_TX_SIGNATURE_UNAUTHORIZED);

			}

		} catch (TransferException ex) {

			log.error("Error: " + ex.getError());

			throw ex;

		} catch (Exception e) {

			log.error("Error: ", e);

			throw new TransferException(TransferResponse.ERROR_SYSTEM);

		}

		log.debug("New UnconfirmedTransfer: " + c);

		return c;

	}

}
