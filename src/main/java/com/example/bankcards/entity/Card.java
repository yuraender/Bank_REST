package com.example.bankcards.entity;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.util.CardUtil;
import com.example.bankcards.util.EncryptionService;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "cards")
@Getter
@Setter
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String number;

    @Column(nullable = false)
    private String numberHash;

    @Column(nullable = false)
    private String holder;

    @Column(nullable = false)
    private LocalDate expiryDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(nullable = false)
    private BigDecimal balance;

    @Column(nullable = false)
    private boolean deleted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Status getStatus() {
        if (expiryDate.isBefore(LocalDate.now())) {
            return Status.EXPIRED;
        }
        return status;
    }

    public CardDto toDto(EncryptionService encryptionService) {
        return new CardDto(
                id, CardUtil.mask(encryptionService.decrypt(number)),
                holder, expiryDate, getStatus(), balance,
                deleted
        );
    }

    public enum Status {

        ACTIVE, BLOCKED, EXPIRED
    }
}
