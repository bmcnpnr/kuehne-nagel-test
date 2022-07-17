package com.kuehne.nagel.service;

import com.kuehne.nagel.entity.WalletEntity;
import com.kuehne.nagel.exception.WalletNotFoundException;
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

    @Autowired
    public void setWalletRepository(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }
}
