package io.zdp.node.service.validation.consensus;

import java.math.BigDecimal;

import org.apache.commons.lang3.ArrayUtils;
import org.bouncycastle.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.zdp.api.model.v1.TransferResponse;
import io.zdp.crypto.Hashing;
import io.zdp.model.network.NetworkTopologyService;
import io.zdp.node.service.validation.cache.TransferConsensusCache;
import io.zdp.node.service.validation.getAccounts.GetNodeAccountsResponse;
import io.zdp.node.service.validation.getAccounts.GetNodeAccountsResponse.Status;
import io.zdp.node.service.validation.model.UnconfirmedTransfer;
import io.zdp.node.service.validation.service.ValidationNodeSigner;
import io.zdp.node.service.validation.settle.TransferSettlementRequest;
import io.zdp.node.service.validation.settle.TransferSettlementRequestTopicPublisher;
import io.zdp.node.service.validation.settle.TransferSettlementService;
import io.zdp.node.storage.account.domain.Account;
import io.zdp.node.storage.transfer.dao.CurrentTransferDao;
import io.zdp.node.storage.transfer.domain.CurrentTransfer;

@Service
public class TransferConsensusService {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private TransferConsensusCache transferConsensusCache;

	@Autowired
	private NetworkTopologyService networkService;

	@Autowired
	private CurrentTransferDao currentTransferDao;

	@Autowired
	private TransferSettlementService transferSettlementService;

	@Autowired
	private TransferSettlementRequestTopicPublisher TransferSettlementTopicPublisher;

	@Autowired
	private ValidationNodeSigner validationNodeSigner;

	public void process(final UnconfirmedTransfer transfer) {

		log.debug("Consensus: " + transfer);

		synchronized (transfer) {

			if (transferConsensusCache.contains(transfer)) {
				log.debug("Already in consensus process, ignore: " + transfer);
				return;
			}

			// If less than 50% confirmation received, do not settle
			if (transfer.getConfirmations().size() < networkService.getAllNodes().size() / 2) {

				log.warn("Not going to consensus as not enough confirmations: " + transfer.getTransactionUuid());

				logErrorTransfer(transfer, TransferResponse.ERROR_NOT_CONFIRMED);

				return;
			}

			transferConsensusCache.add(transfer);
		}

		Account fromAccount = null;

		Account toAccount = null;

		for (final GetNodeAccountsResponse accounts : transfer.getConfirmations()) {

			if (Status.ACCOUNT_LOCKED.equals(accounts.getStatus())) {

				logErrorTransfer(transfer, TransferResponse.ERROR_LOCKED_ACCOUNTS);

				return;

			} else if (Status.REPLAY_DETECTED.equals(accounts.getStatus())) {

				logErrorTransfer(transfer, TransferResponse.ERROR_TX_REPLAY);

				return;

			} else if (Status.UNAUTHORIZED.equals(accounts.getStatus())) {

				logErrorTransfer(transfer, TransferResponse.ERROR_SYSTEM);

				return;

			} else if (Status.OK.equals(accounts.getStatus())) {

				final Account from = accounts.getFromAccount();

				if (fromAccount == null || from.getHeight() > fromAccount.getHeight()) {

					if (from != null && Arrays.areEqual(from.getUuidAsBytes(), transfer.getFromAccountUuid().getPublicKeyHash())) {
						fromAccount = from;
					}
				}

				final Account to = accounts.getToAccount();

				if (toAccount == null || to.getHeight() > toAccount.getHeight()) {

					if (to != null && Arrays.areEqual(to.getUuidAsBytes(), transfer.getToAccountUuid().getPublicKeyHash())) {
						toAccount = to;
					}

				}
			}
		}

		// Perform validation
		if (fromAccount == null) {
			logErrorTransfer(transfer, TransferResponse.ERROR_INVALID_FROM_ACCOUNT);
			return;
		}

		// Enough funds?
		if (fromAccount.getBalance().compareTo(transfer.getAmount().add(transfer.getFee())) < 0) {
			logErrorTransfer(transfer, TransferResponse.ERROR_INSUFFICIENT_FUNDS);
			return;
		}

		// Create TO account if required
		if (toAccount == null) {
			toAccount = new Account();
			toAccount.setBalance(BigDecimal.ZERO);
			toAccount.setCurve(transfer.getToAccountUuid().getCurveAsIndex());
			toAccount.setHeight(0);
			toAccount.setTransferHash(new byte[] {});
			toAccount.setUuid(transfer.getToAccountUuid().getPublicKeyHash());
		}
		
		// Update accounts block chain signatures with the new transfer
		updateFromAccount(fromAccount, transfer);
		updateToAccount(toAccount, transfer);

		log.debug("Settled on FROM [" + fromAccount + "] and TO [" + toAccount + "]");

		// Create settlement object
		final TransferSettlementRequest settlement = new TransferSettlementRequest();
		settlement.setCurrentTransfer(transfer.toCurrentTransfer());
		settlement.setFromAccount(fromAccount);
		settlement.setToAccount(toAccount);
		settlement.setTransferUuid(transfer.getTransactionSignature());

		try {
			validationNodeSigner.sign(settlement);
		} catch (Exception e) {
			log.error("Error: ", e);
		}

		// Settle locally
		this.transferSettlementService.settle(settlement);

		// Broadcase settlement
		this.TransferSettlementTopicPublisher.send(settlement);

	}

	private void updateFromAccount(Account from, UnconfirmedTransfer transfer) {
		
		from.setBalance(from.getBalance().subtract( transfer.getAmount()).subtract(transfer.getFee()));
		from.setHeight(from.getHeight() + 1);
		
		byte[] signature = ArrayUtils.addAll(from.getTransferHash(), transfer.getTransactionSignature());
		signature = Hashing.ripemd160(signature); 
				
		from.setTransferHash(signature);

		
	}
	
	private void updateToAccount(Account to, UnconfirmedTransfer transfer) {
		
		to.setBalance(to.getBalance().add( transfer.getAmount()));
		to.setHeight(to.getHeight() + 1);
		
		byte[] signature = ArrayUtils.addAll(to.getTransferHash(), transfer.getTransactionSignature());
		signature = Hashing.ripemd160(signature); 
				
		to.setTransferHash(signature);
		
	}

	private void logErrorTransfer(final UnconfirmedTransfer transfer, String errorMsg) {
		CurrentTransfer currentTransfer = transfer.toCurrentTransfer();
		currentTransfer.setStatus(errorMsg);
		currentTransferDao.save(currentTransfer);
	}

}
