package io.zdp.node.dao.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.zdp.node.domain.Transfer;

@Repository
public interface TransferDao extends JpaRepository<Transfer, Long> {

	Transfer findByUuid(byte[] uuid);
	
}
