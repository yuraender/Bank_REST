package com.example.bankcards.entity;

import com.example.bankcards.dto.TransactionDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Getter
@Setter
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_id", nullable = false)
    private Card from;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_id", nullable = false)
    private Card to;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(length = 100, nullable = false)
    private String comment;

    @Column(nullable = false)
    private LocalDateTime date;

    public TransactionDto toDto() {
        return new TransactionDto(id, from.getId(), to.getId(), amount, comment, date);
    }
}
