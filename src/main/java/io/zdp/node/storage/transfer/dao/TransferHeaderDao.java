package io.zdp.node.storage.transfer.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.zdp.node.storage.transfer.domain.TransferHeader;

@Repository
public interface TransferHeaderDao extends JpaRepository<TransferHeader, Long> {

	TransferHeader findByUuid(byte[] uuid);

}
