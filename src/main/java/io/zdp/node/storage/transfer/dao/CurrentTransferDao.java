package io.zdp.node.storage.transfer.dao;

import io.zdp.node.storage.transfer.domain.CurrentTransfer;

public interface CurrentTransferDao {

	void save(CurrentTransfer t);
}
