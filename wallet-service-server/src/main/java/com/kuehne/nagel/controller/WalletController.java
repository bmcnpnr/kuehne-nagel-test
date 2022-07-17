package com.kuehne.nagel.controller;

import com.kuehne.nagel.exception.InsufficientFundsException;
import com.kuehne.nagel.exception.WalletNotFoundException;
import com.kuehne.nagel.model.WalletTransferModel;
import com.kuehne.nagel.service.WalletService;
import com.kuehne.nagel.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
public class WalletController {

    private WalletService walletService;

    @GetMapping("/test")
    public String test() {
        return "wallet-test";
    }

    @GetMapping("/wallets")
    public ResponseEntity<?> getAllWallets() {
        return ResponseEntity.ok(walletService.getAllWallets());
    }

    @PostMapping("/wallets")
    public ResponseEntity<String> createNewWallet() {
        return ResponseEntity.ok(walletService.createWallet().getWalletId().toString());
    }

    @PatchMapping("/wallets/{walletId}/top-up/{amount}")
    public ResponseEntity<String> topUpWallet(@PathVariable Long walletId, @PathVariable BigDecimal amount) {
        try {
            walletService.topUpWallet(walletId, amount);
            return ResponseEntity.ok("Wallet with id: " + walletId + " topped up by " + amount);
        } catch (WalletNotFoundException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Constants.WALLET_WAS_NOT_FOUND);
        }
    }

    @GetMapping("/wallets/{walletId}")
    public ResponseEntity<String> checkWalletBalance(@PathVariable Long walletId) {
        try {
            return ResponseEntity.ok(walletService.getBalance(walletId).toPlainString());
        } catch (WalletNotFoundException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Constants.WALLET_WAS_NOT_FOUND);
        }
    }

    @PatchMapping("/wallets/{walletId}/withdrawal/{amount}")
    public ResponseEntity<String> withdrawFromWallet(@PathVariable Long walletId, @PathVariable BigDecimal amount) {
        try {
            walletService.withdrawFromWallet(walletId, amount);
            return ResponseEntity.ok("Wallet with id: " + walletId + " withdraw by " + amount);
        } catch (WalletNotFoundException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Constants.WALLET_WAS_NOT_FOUND);
        } catch (InsufficientFundsException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Constants.WALLET_HAS_INSUFFICIENT_FUNDS);
        }
    }

    @PatchMapping("/wallets/transfer")
    public ResponseEntity<?> walletToWalletTransaction(@RequestBody WalletTransferModel walletTransferModel) {
        if (walletTransferModel.getFirstWalletId().equals(walletTransferModel.getSecondWalletId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Wallet ids are the same");
        }
        try {
            walletService.transferFundsBetweenWallets(walletTransferModel);
            return ResponseEntity.ok("Transferred " + walletTransferModel.getTransferAmount() + " from Wallet with id: "
                    + walletTransferModel.getFirstWalletId() + " to Wallet with id: " + walletTransferModel.getSecondWalletId());
        } catch (WalletNotFoundException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Constants.WALLET_WAS_NOT_FOUND);

        } catch (InsufficientFundsException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Constants.WALLET_HAS_INSUFFICIENT_FUNDS);

        }
    }

    @Autowired
    public void setWalletService(WalletService walletService) {
        this.walletService = walletService;
    }
}
