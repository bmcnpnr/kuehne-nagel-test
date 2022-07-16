package com.kuehne.nagel.controller;

import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
public class WalletController {

    @GetMapping("/test")
    public String test() {
        return "wallet-test";
    }

    @GetMapping("/wallets")
    public String getAllWallets() {
        return "wallets";
    }

    @PostMapping("/wallets")
    public String createNewWallet() {
        return "wallets";
    }

    @PatchMapping("/wallets/{id}/top-up/{amount}")
    public String topUpWallet(@PathVariable String id, @PathVariable BigDecimal amount) {
        return "wallets top up";
    }

    @GetMapping("/wallets/{id}")
    public String checkWalletBalance() {
        return "wallets check balance";
    }

    @PatchMapping("/wallets/{id}/withdrawal/{amount}")
    public String withdrawFromWallet(@PathVariable String id, @PathVariable BigDecimal amount) {
        return "wallets withdraw";
    }
}
