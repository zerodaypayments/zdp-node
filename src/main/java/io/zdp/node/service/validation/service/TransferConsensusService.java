package io.zdp.node.service.validation.service;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.zdp.api.model.v1.TransferResponse;
import io.zdp.model.network.NetworkTopologyService;
import io.zdp.node.service.validation.cache.TransferConsensusCache;
import io.zdp.node.service.validation.cache.key.ByteWrapper;
import io.zdp.node.service.validation.getAccounts.GetNodeAccountsResponse;
import io.zdp.node.service.validation.model.UnconfirmedTransfer;
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

	public void settle(final UnconfirmedTransfer transfer) {

		log.debug("Settle: " + transfer);

		synchronized (transfer) {

			if (transferConsensusCache.contains(transfer)) {
				log.debug("Already in settlement, ignore: " + transfer);
				return;
			}

			// If less than 50% confirmation received, do not settle
			if (transfer.getConfirmations().size() < networkService.getAllNodes().size() / 2) {

				log.warn("Not going to settle as not enough confirmations: " + transfer.getTransactionUuid());

				CurrentTransfer currentTransfer = transfer.toCurrentTransfer();
				currentTransfer.setStatus(TransferResponse.ERROR_NOT_CONFIRMED);
				currentTransferDao.save(currentTransfer);

				return;
			}

			transferConsensusCache.add(transfer);
		}

		final Map<ByteWrapper, Integer> fromAccountsHashes = new HashMap<>();
		final Map<ByteWrapper, Integer> toAccountsHashes = new HashMap<>();

		for (final GetNodeAccountsResponse accounts : transfer.getConfirmations()) {

			// Group by FROM accounts block signatures
			if (accounts.getFromAccount() != null) {

				final ByteWrapper bw = new ByteWrapper(accounts.getFromAccount().toHashSignature());

				int occurences = fromAccountsHashes.containsKey(bw) ? fromAccountsHashes.get(bw) : 0;

				occurences++;

				fromAccountsHashes.put(bw, occurences);

			}

			// Group by TO accounts block signatures
			if (accounts.getToAccount() != null) {

				final ByteWrapper bw = new ByteWrapper(accounts.getToAccount().toHashSignature());

				int occurences = toAccountsHashes.containsKey(bw) ? toAccountsHashes.get(bw) : 0;

				occurences++;

				toAccountsHashes.put(bw, occurences);

			}

		}

	}

}
