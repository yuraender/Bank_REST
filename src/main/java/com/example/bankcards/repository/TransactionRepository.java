package com.example.bankcards.repository;

import com.example.bankcards.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Page<Transaction> findByFromIdOrToId(Long fromId, Long toId, Pageable pageable);

    @Query("SELECT t FROM Transaction t WHERE t.from.user.id = :userId OR t.to.user.id = :userId")
    Page<Transaction> findByUserId(@Param("userId") Long userId, Pageable pageable);
}
