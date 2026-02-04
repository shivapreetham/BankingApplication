package com.banking.repository;

import com.banking.model.Transaction;
import com.banking.model.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * JPA Repository for Transaction entity
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

    Page<Transaction> findByAccountOrderByTimestampDesc(Account account, Pageable pageable);

    List<Transaction> findByAccount(Account account);

    @Query("SELECT t FROM Transaction t WHERE t.account = ?1 ORDER BY t.timestamp DESC")
    List<Transaction> findRecentTransactions(Account account, Pageable pageable);
}
