package com.playtomic.tests.wallet.domain.transaction.exception;

public class TransactionAmountTooSmallException extends RuntimeException {
    public TransactionAmountTooSmallException() {
        super("Amount too small");
    }

    public TransactionAmountTooSmallException(String message) { super(message); }

    public TransactionAmountTooSmallException(String message, Throwable cause) { super(message, cause); }
}
