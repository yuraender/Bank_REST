package com.example.bankcards.repository;

import com.example.bankcards.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CardRepository extends JpaRepository<Card, Long> {

    boolean existsByNumberHash(String numberHash);

    List<Card> findByUserId(Long userId);
}
