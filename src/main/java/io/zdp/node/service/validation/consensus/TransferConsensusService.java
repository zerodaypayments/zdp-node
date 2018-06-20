package io.zdp.node.service.validation.consensus;

import org.bouncycastle.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.zdp.api.model.v1.TransferResponse;
import io.zdp.model.network.NetworkTopologyService;
import io.zdp.node.service.validation.cache.TransferConsensusCache;
import io.zdp.node.service.validation.getAccounts.GetNodeAccountsResponse;
import io.zdp.node.service.validation.getAccounts.GetNodeAccountsResponse.Status;
import io.zdp.node.service.validation.model.UnconfirmedTransfer;
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

		log.debug("Settled on FROM [" + fromAccount + "] and TO [" + toAccount + "]");

		// TODO this.transferSettlementService.settle(transfer, fromAccount, toAccount);

		// 		 TODO publish settlments event 
	}

	private void logErrorTransfer(final UnconfirmedTransfer transfer, String errorMsg) {
		CurrentTransfer currentTransfer = transfer.toCurrentTransfer();
		currentTransfer.setStatus(errorMsg);
		currentTransferDao.save(currentTransfer);
	}

}
