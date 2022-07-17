package com.kuehne.nagel.service;

import com.kuehne.nagel.entity.WalletEntity;
import com.kuehne.nagel.exception.InsufficientFundsException;
import com.kuehne.nagel.exception.WalletNotFoundException;
import com.kuehne.nagel.model.WalletTransferModel;
import com.kuehne.nagel.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class WalletServiceImpl implements WalletService {

    private WalletRepository walletRepository;

    @Override
    @Transactional(readOnly = true)
    public List<WalletEntity> getAllWallets() {
        return walletRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getBalance(Long walletId) throws WalletNotFoundException {
        Optional<WalletEntity> byId = walletRepository.findById(walletId);
        if(byId.isPresent()) {
            return byId.get().getBalance();
        } else {
            throw new WalletNotFoundException();
        }
    }

    @Override
    @Transactional
    public WalletEntity createWallet() {
        WalletEntity walletEntity = new WalletEntity();
        walletRepository.save(walletEntity);
        return walletEntity;
    }

    @Override
    public void topUpWallet(Long walletId, BigDecimal amount) throws WalletNotFoundException {
        Optional<WalletEntity> byId = walletRepository.findById(walletId);
        if (!byId.isPresent()) {
            throw new WalletNotFoundException();
        } else {
            WalletEntity walletEntity = byId.get();
            walletEntity.setBalance(walletEntity.getBalance().add(amount));
            walletRepository.save(walletEntity);
        }
    }

    @Override
    public void withdrawFromWallet(Long walletId, BigDecimal amount) throws WalletNotFoundException, InsufficientFundsException {
        Optional<WalletEntity> byId = walletRepository.findById(walletId);
        if (!byId.isPresent()) {
            throw new WalletNotFoundException();
        } else {
            WalletEntity walletEntity = byId.get();
            if (walletEntity.getBalance().compareTo(amount) < 0) {
                throw new InsufficientFundsException();
            }
            walletEntity.setBalance(walletEntity.getBalance().subtract((amount)));
            walletRepository.save(walletEntity);
        }
    }

    @Override
    public void transferFundsBetweenWallets(WalletTransferModel walletTransferModel) throws WalletNotFoundException, InsufficientFundsException {
        Optional<WalletEntity> byIdFirst = walletRepository.findById(walletTransferModel.getFirstWalletId());
        Optional<WalletEntity> byIdSecond = walletRepository.findById(walletTransferModel.getSecondWalletId());
        if (byIdFirst.isPresent() && byIdSecond.isPresent()) {
            WalletEntity firstWallet = byIdFirst.get();
            WalletEntity secondWallet = byIdSecond.get();
            BigDecimal transferAmount = walletTransferModel.getTransferAmount();
            if (firstWallet.getBalance().compareTo(transferAmount) < 0) {
                throw new InsufficientFundsException();
            }
            firstWallet.setBalance(firstWallet.getBalance().subtract(transferAmount));
            secondWallet.setBalance(secondWallet.getBalance().add(transferAmount));
            walletRepository.save(firstWallet);
            walletRepository.save(secondWallet);
        } else {
            throw new WalletNotFoundException();
        }
    }

    @Autowired
    public void setWalletRepository(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }
}
