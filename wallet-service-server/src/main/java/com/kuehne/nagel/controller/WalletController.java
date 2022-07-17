package com.kuehne.nagel.controller;

import com.kuehne.nagel.exception.InsufficientFundsException;
import com.kuehne.nagel.exception.WalletNotFoundException;
import com.kuehne.nagel.model.WalletTransferModel;
import com.kuehne.nagel.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

//TODO put strings into constant values

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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Wallet was not found");
        }
    }

    @GetMapping("/wallets/{walletId}")
    public ResponseEntity<String> checkWalletBalance(@PathVariable Long walletId) {
        try {
            return ResponseEntity.ok(walletService.getBalance(walletId).toPlainString());
        } catch (WalletNotFoundException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Wallet was not found");
        }
    }

    @PatchMapping("/wallets/{walletId}/withdrawal/{amount}")
    public ResponseEntity<String> withdrawFromWallet(@PathVariable Long walletId, @PathVariable BigDecimal amount) {
        try {
            walletService.withdrawFromWallet(walletId, amount);
            return ResponseEntity.ok("Wallet with id: " + walletId + " withdraw by " + amount);
        } catch (WalletNotFoundException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Wallet was not found");
        } catch (InsufficientFundsException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Wallet has insufficient funds");
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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Wallet was not found");

        } catch (InsufficientFundsException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Wallet has insufficient funds");

        }
    }

    @Autowired
    public void setWalletService(WalletService walletService) {
        this.walletService = walletService;
    }
}
