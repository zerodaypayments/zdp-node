package io.zdp.node.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import io.zdp.node.storage.transfer.dao.CurrentTransferDao;

@Component
public class TestGenerator {

	@Autowired
	private CurrentTransferDao dao;

	int i = 0;

	@Scheduled(fixedDelay = 10000)
	public void run() {
/*
		for (int j = 0; j < 1; j++) {
			CurrentTransfer t = new CurrentTransfer();

			ZDPKeyPair from = ZDPKeyPair.createRandom(Curves.DEFAULT_CURVE);
			ZDPKeyPair to = ZDPKeyPair.createRandom(Curves.DEFAULT_CURVE);

			t.setAmount( RandomUtils.nextLong(0, Long.MAX_VALUE));
			t.setDate(System.currentTimeMillis());
			t.setFee(1000);
			t.setFrom(from.getZDPAccount().getUuid());
			t.setTo(to.getZDPAccount().getUuid());
			t.setUuid("tx" + Base58.encode(Hashing.hashTransactionSignature(t.getFrom() + t.getAmount() + t.getTo())) + "z");

			dao.save(t);
			i++;
		}

		System.out.println(i);
*/
	}

}
