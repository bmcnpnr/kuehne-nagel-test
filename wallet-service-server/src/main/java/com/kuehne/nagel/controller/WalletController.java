package com.kuehne.nagel.controller;

import com.kuehne.nagel.exception.WalletNotFoundException;
import com.kuehne.nagel.model.WalletTransferModel;
import com.kuehne.nagel.service.WalletService;
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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Wallet was not found");
        }
    }

    @GetMapping("/wallets/{id}")
    public ResponseEntity<String> checkWalletBalance(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(walletService.getBalance(id).toPlainString());
        } catch (WalletNotFoundException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Wallet was not found");
        }
    }

    @PatchMapping("/wallets/{id}/withdrawal/{amount}")
    public String withdrawFromWallet(@PathVariable Long id, @PathVariable BigDecimal amount) {
        return "wallets withdraw";
    }

    @PatchMapping("/wallets/transfer")
    public String walletToWalletTransaction(@RequestBody WalletTransferModel walletTransferModel) {
        return "wallet to wallet transaction";
    }

    @Autowired
    public void setWalletService(WalletService walletService) {
        this.walletService = walletService;
    }
}
