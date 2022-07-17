package com.kuehne.nagel.service;

import com.kuehne.nagel.entity.WalletEntity;
import com.kuehne.nagel.exception.InsufficientFundsException;
import com.kuehne.nagel.exception.WalletNotFoundException;
import com.kuehne.nagel.model.WalletTransferModel;
import com.kuehne.nagel.repository.WalletRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class WalletServiceTest {

    @Mock
    private WalletRepository walletRepository;

    private WalletServiceImpl walletService;

    private WalletEntity entity1;

    @BeforeAll
    void setUp() {
        walletRepository = mock(WalletRepository.class);

        walletService = new WalletServiceImpl();
        walletService.setWalletRepository(walletRepository);

        entity1 = new WalletEntity();
        entity1.setWalletId(1L);
        entity1.setBalance(new BigDecimal("100.00"));

        when(walletRepository.findById(1L)).thenReturn(Optional.of(entity1));
        when(walletRepository.save(new WalletEntity())).thenReturn(new WalletEntity());
    }

    @Test
    public void test_getAllWallets() {
        List<WalletEntity> walletEntities = new ArrayList<>();
        walletEntities.add(entity1);

        when(walletRepository.findAll()).thenReturn(walletEntities);

        List<WalletEntity> allWallets = walletService.getAllWallets();
        assertEquals(walletEntities, allWallets);
    }

    @Test
    public void test_getBalance() throws WalletNotFoundException {
        WalletEntity value = new WalletEntity();
        value.setBalance(new BigDecimal("56.00"));
        when(walletRepository.findById(7L)).thenReturn(Optional.of(value));

        BigDecimal balance = walletService.getBalance(7L);

        assertEquals(new BigDecimal("56.00"), balance);
    }

    @Test
    public void test_getBalance_Exception() {
        when(walletRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(WalletNotFoundException.class, () -> {
            walletService.getBalance(2L);
        });

    }

    @Test
    public void test_createWallet() {
        walletService.createWallet();
    }

    @Test
    public void test_topUpWallet() throws WalletNotFoundException {
        WalletEntity value = new WalletEntity();
        value.setBalance(new BigDecimal("56.00"));
        when(walletRepository.findById(8L)).thenReturn(Optional.of(value));
        walletService.topUpWallet(8L, new BigDecimal("100.00"));

        assertEquals(new BigDecimal("156.00"), value.getBalance());
    }

    @Test
    public void test_topUpWallet_Exception() throws WalletNotFoundException {
        when(walletRepository.findById(3L)).thenReturn(Optional.empty());
        assertThrows(WalletNotFoundException.class, () -> {
            walletService.topUpWallet(3L, new BigDecimal("100.00"));
        });
    }

    @Test
    public void test_withdrawFromWallet() throws WalletNotFoundException, InsufficientFundsException {
        WalletEntity value = new WalletEntity();
        value.setBalance(new BigDecimal("56.00"));
        when(walletRepository.findById(9L)).thenReturn(Optional.of(value));
        walletService.withdrawFromWallet(9L, new BigDecimal("36.00"));

        assertEquals(new BigDecimal("20.00"), value.getBalance());
    }

    @Test
    public void test_withdrawFromWallet_WalletNotFoundException() {
        when(walletRepository.findById(4L)).thenReturn(Optional.empty());
        assertThrows(WalletNotFoundException.class, () -> {
            walletService.withdrawFromWallet(4L, new BigDecimal("100.00"));
        });
    }

    @Test
    public void test_withdrawFromWallet_InsufficientFundsException() {
        assertThrows(InsufficientFundsException.class, () -> {
            walletService.withdrawFromWallet(1L, new BigDecimal("500.00"));
        });
    }

    @Test
    public void test_transferFundsBetweenWallets() throws WalletNotFoundException, InsufficientFundsException {
        WalletEntity walletEntity1 = new WalletEntity();
        walletEntity1.setWalletId(5L);
        walletEntity1.setBalance(new BigDecimal("100.00"));
        when(walletRepository.findById(5L)).thenReturn(Optional.of(walletEntity1));

        WalletEntity walletEntity2 = new WalletEntity();
        walletEntity2.setWalletId(6L);
        walletEntity2.setBalance(new BigDecimal("0.00"));
        when(walletRepository.findById(6L)).thenReturn(Optional.of(walletEntity2));

        WalletTransferModel walletTransferModel = new WalletTransferModel();
        walletTransferModel.setFirstWalletId(5L);
        walletTransferModel.setSecondWalletId(6L);
        walletTransferModel.setTransferAmount(new BigDecimal("50.00"));

        walletService.transferFundsBetweenWallets(walletTransferModel);

        assertEquals(new BigDecimal("50.00"), walletEntity1.getBalance());
        assertEquals(new BigDecimal("50.00"), walletEntity2.getBalance());
    }

    @Test
    public void test_transferFundsBetweenWallets_InsufficientFundsException() {
        WalletEntity walletEntity1 = new WalletEntity();
        walletEntity1.setWalletId(10L);
        walletEntity1.setBalance(new BigDecimal("100.00"));
        when(walletRepository.findById(10L)).thenReturn(Optional.of(walletEntity1));

        WalletEntity walletEntity2 = new WalletEntity();
        walletEntity2.setWalletId(11L);
        walletEntity2.setBalance(new BigDecimal("0.00"));
        when(walletRepository.findById(11L)).thenReturn(Optional.of(walletEntity2));

        WalletTransferModel walletTransferModel = new WalletTransferModel();
        walletTransferModel.setFirstWalletId(10L);
        walletTransferModel.setSecondWalletId(11L);
        walletTransferModel.setTransferAmount(new BigDecimal("150.00"));

        assertThrows(InsufficientFundsException.class, () -> {
            walletService.transferFundsBetweenWallets(walletTransferModel);
        });
    }

    @Test
    public void test_transferFundsBetweenWallets_WalletNotFoundException() {
        WalletTransferModel walletTransferModel = new WalletTransferModel();
        walletTransferModel.setFirstWalletId(12L);
        walletTransferModel.setSecondWalletId(13L);
        walletTransferModel.setTransferAmount(new BigDecimal("150.00"));

        assertThrows(WalletNotFoundException.class, () -> {
            walletService.transferFundsBetweenWallets(walletTransferModel);
        });
    }

}
