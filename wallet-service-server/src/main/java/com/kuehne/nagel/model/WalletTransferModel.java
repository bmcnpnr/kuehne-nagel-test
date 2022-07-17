package com.kuehne.nagel.model;

import java.math.BigDecimal;

public class WalletTransferModel {
    private Long firstWalletId;
    private Long secondWalletId;
    private BigDecimal transferAmount;

    public Long getFirstWalletId() {
        return firstWalletId;
    }

    public void setFirstWalletId(Long firstWalletId) {
        this.firstWalletId = firstWalletId;
    }

    public Long getSecondWalletId() {
        return secondWalletId;
    }

    public void setSecondWalletId(Long secondWalletId) {
        this.secondWalletId = secondWalletId;
    }

    public BigDecimal getTransferAmount() {
        return transferAmount;
    }

    public void setTransferAmount(BigDecimal transferAmount) {
        this.transferAmount = transferAmount;
    }
}
