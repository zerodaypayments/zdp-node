package io.zdp.node.storage.account.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import io.zdp.node.storage.account.domain.Account;

public interface AccountDao extends JpaRepository<Account, Long> {

	Account findByUuid(byte[] uuid);

}
