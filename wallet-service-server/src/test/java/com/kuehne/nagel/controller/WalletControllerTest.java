package com.kuehne.nagel.controller;

import com.kuehne.nagel.entity.WalletEntity;
import com.kuehne.nagel.exception.InsufficientFundsException;
import com.kuehne.nagel.exception.WalletNotFoundException;
import com.kuehne.nagel.model.WalletTransferModel;
import com.kuehne.nagel.service.WalletService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class WalletControllerTest {

    @Mock
    private WalletService walletService;

    private WalletController walletController;

    @BeforeAll
    void setUp() {
        walletService = Mockito.mock(WalletService.class);
        walletController = new WalletController();
        walletController.setWalletService(walletService);

        List<WalletEntity> walletEntities = new ArrayList<>();
        WalletEntity entity1 = new WalletEntity();
        entity1.setBalance(new BigDecimal("100.00"));
        entity1.setWalletId(1L);
        walletEntities.add(entity1);

        WalletEntity entity2 = new WalletEntity();
        entity2.setBalance(new BigDecimal("100.00"));
        entity2.setWalletId(2L);

        when(walletService.getAllWallets()).thenReturn(walletEntities);

        when(walletService.createWallet()).thenReturn(entity2);
    }

    @Test
    public void test_Test() {
        String testResult = walletController.test();
        assertEquals("wallet-test", testResult);
    }

    @Test
    public void test_getAllWallets() {
        ResponseEntity<?> allWallets = walletController.getAllWallets();
        assertEquals(HttpStatus.OK, allWallets.getStatusCode());

        ((ArrayList<WalletEntity>) allWallets.getBody()).forEach(item -> {
            assertEquals(new BigDecimal("100.00"), item.getBalance());
            assertEquals(1L, item.getWalletId());
        });
    }

    @Test
    public void test_createNewWallet() {
        ResponseEntity<String> newWallet = walletController.createNewWallet();
        assertEquals(HttpStatus.OK, newWallet.getStatusCode());

        assertEquals("2", newWallet.getBody());
    }

    @Test
    public void test_topUpWallet() throws WalletNotFoundException {
        doNothing().when(walletService).topUpWallet(1L, new BigDecimal("100.00"));

        ResponseEntity<String> stringResponseEntity = walletController.topUpWallet(1L, new BigDecimal("100.00"));

        assertEquals(HttpStatus.OK, stringResponseEntity.getStatusCode());

        assertEquals("Wallet with id: 1 topped up by 100.00", stringResponseEntity.getBody());
    }

    @Test
    public void test_topUpWalletException() throws WalletNotFoundException {
        doThrow(WalletNotFoundException.class).when(walletService).topUpWallet(1L, new BigDecimal("100.00"));

        ResponseEntity<String> stringResponseEntity = walletController.topUpWallet(1L, new BigDecimal("100.00"));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, stringResponseEntity.getStatusCode());

        assertEquals("Wallet was not found", stringResponseEntity.getBody());
    }

    @Test
    public void test_checkWalletBalance() throws WalletNotFoundException {
        when(walletService.getBalance(2L)).thenReturn(new BigDecimal("100.00"));

        ResponseEntity<String> stringResponseEntity = walletController.checkWalletBalance(2L);

        assertEquals(HttpStatus.OK, stringResponseEntity.getStatusCode());

        assertEquals("100.00", stringResponseEntity.getBody());
    }

    @Test
    public void test_checkWalletBalanceException() throws WalletNotFoundException {
        doThrow(WalletNotFoundException.class).when(walletService).getBalance(1L);

        ResponseEntity<String> stringResponseEntity = walletController.checkWalletBalance(1L);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, stringResponseEntity.getStatusCode());

        assertEquals("Wallet was not found", stringResponseEntity.getBody());
    }

    @Test
    public void test_withdrawFromWallet() throws WalletNotFoundException, InsufficientFundsException {
        doNothing().when(walletService).withdrawFromWallet(1L, new BigDecimal("100.00"));

        ResponseEntity<String> stringResponseEntity = walletController.withdrawFromWallet(1L, new BigDecimal("100.00"));

        assertEquals(HttpStatus.OK, stringResponseEntity.getStatusCode());

        assertEquals("Wallet with id: 1 withdraw by 100.00", stringResponseEntity.getBody());
    }

    @Test
    public void test_withdrawFromWallet_InsufficientFundsException() throws WalletNotFoundException, InsufficientFundsException {
        doThrow(InsufficientFundsException.class).when(walletService).withdrawFromWallet(1L, new BigDecimal("100.00"));

        ResponseEntity<String> stringResponseEntity = walletController.withdrawFromWallet(1L, new BigDecimal("100.00"));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, stringResponseEntity.getStatusCode());

        assertEquals("Wallet has insufficient funds", stringResponseEntity.getBody());
    }

    @Test
    public void test_withdrawFromWallet_WalletNotFoundException() throws WalletNotFoundException, InsufficientFundsException {
        doThrow(WalletNotFoundException.class).when(walletService).withdrawFromWallet(1L, new BigDecimal("100.00"));

        ResponseEntity<String> stringResponseEntity = walletController.withdrawFromWallet(1L, new BigDecimal("100.00"));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, stringResponseEntity.getStatusCode());

        assertEquals("Wallet was not found", stringResponseEntity.getBody());
    }

    @Test
    public void test_walletToWalletTransaction() throws WalletNotFoundException, InsufficientFundsException {
        WalletTransferModel walletTransferModel = new WalletTransferModel();
        walletTransferModel.setFirstWalletId(1L);
        walletTransferModel.setSecondWalletId(2L);
        walletTransferModel.setTransferAmount(new BigDecimal("100.00"));
        doNothing().when(walletService).transferFundsBetweenWallets(walletTransferModel);

        ResponseEntity<?> responseEntity = walletController.walletToWalletTransaction(walletTransferModel);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        assertEquals("Transferred 100.00 from Wallet with id: 1 to Wallet with id: 2", responseEntity.getBody());
    }

    @Test
    public void test_walletToWalletTransaction_sameWallets() {
        WalletTransferModel walletTransferModel = new WalletTransferModel();
        walletTransferModel.setFirstWalletId(1L);
        walletTransferModel.setSecondWalletId(1L);
        walletTransferModel.setTransferAmount(new BigDecimal("100.00"));

        ResponseEntity<?> responseEntity = walletController.walletToWalletTransaction(walletTransferModel);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

        assertEquals("Wallet ids are the same", responseEntity.getBody());
    }

    @Test
    public void test_walletToWalletTransaction_WalletNotFoundException() throws WalletNotFoundException, InsufficientFundsException {
        WalletTransferModel walletTransferModel = new WalletTransferModel();
        walletTransferModel.setFirstWalletId(1L);
        walletTransferModel.setSecondWalletId(2L);
        walletTransferModel.setTransferAmount(new BigDecimal("100.00"));
        doThrow(WalletNotFoundException.class).when(walletService).transferFundsBetweenWallets(walletTransferModel);

        ResponseEntity<?> responseEntity = walletController.walletToWalletTransaction(walletTransferModel);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());

        assertEquals("Wallet was not found", responseEntity.getBody());
    }

    @Test
    public void test_walletToWalletTransaction_InsufficientFundsException() throws WalletNotFoundException, InsufficientFundsException {
        WalletTransferModel walletTransferModel = new WalletTransferModel();
        walletTransferModel.setFirstWalletId(1L);
        walletTransferModel.setSecondWalletId(2L);
        walletTransferModel.setTransferAmount(new BigDecimal("100.00"));
        doThrow(InsufficientFundsException.class).when(walletService).transferFundsBetweenWallets(walletTransferModel);

        ResponseEntity<?> responseEntity = walletController.walletToWalletTransaction(walletTransferModel);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());

        assertEquals("Wallet has insufficient funds", responseEntity.getBody());
    }

}
