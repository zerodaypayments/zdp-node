package io.zdp.node.dao.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import io.zdp.node.domain.Account;

public interface AccountDao extends JpaRepository<Account, Long> {

	Account findByUuid(byte[] uuid);

}
