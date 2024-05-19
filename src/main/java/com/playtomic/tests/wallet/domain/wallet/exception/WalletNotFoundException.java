package com.playtomic.tests.wallet.domain.wallet.exception;

public class WalletNotFoundException extends RuntimeException {
    public WalletNotFoundException() {
        super("Wallet not found");
    }

    public WalletNotFoundException(String message) { super(message); }

    public WalletNotFoundException(String message, Throwable cause) { super(message, cause); }
}
