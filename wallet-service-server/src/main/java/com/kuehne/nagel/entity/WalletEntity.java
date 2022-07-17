package com.kuehne.nagel.entity;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "WALLET")
public class WalletEntity {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long walletId;

    @Column
    private BigDecimal balance = BigDecimal.ZERO;

    public Long getWalletId() {
        return walletId;
    }

    public void setWalletId(Long walletId) {
        this.walletId = walletId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
