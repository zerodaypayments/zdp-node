package io.zdp.node.storage.transfer.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.zdp.node.storage.transfer.dao.TransferHeaderDao;
import io.zdp.node.storage.transfer.domain.TransferHeader;

@Service
public class TransferHeaderService {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private TransferHeaderDao dao;

	@Transactional(readOnly = false)
	public void save(TransferHeader tx) {
		dao.save(tx);
		log.debug("Saved: " + tx);
	}

	@Transactional(readOnly = true)
	public TransferHeader getByUuid(byte[] uuid) {
		return dao.findByUuid(uuid);
	}

}
