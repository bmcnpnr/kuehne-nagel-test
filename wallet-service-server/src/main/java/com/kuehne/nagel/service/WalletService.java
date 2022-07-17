package com.kuehne.nagel.service;

import com.kuehne.nagel.entity.WalletEntity;
import com.kuehne.nagel.exception.InsufficientFundsException;
import com.kuehne.nagel.exception.WalletNotFoundException;

import java.math.BigDecimal;
import java.util.List;

public interface WalletService {
    List<WalletEntity> getAllWallets();

    BigDecimal getBalance(Long walletId) throws WalletNotFoundException;

    WalletEntity createWallet();

    void topUpWallet(Long walletId, BigDecimal amount) throws WalletNotFoundException;

    void withdrawFromWallet(Long walletId, BigDecimal amount) throws WalletNotFoundException, InsufficientFundsException;
}
