package com.playtomic.tests.wallet.infrastructure.external.persistence;

import com.playtomic.tests.wallet.domain.transaction.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface JpaTransactionRepository extends JpaRepository<Transaction, UUID> {
}
