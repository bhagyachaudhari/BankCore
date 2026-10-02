package com.bankcore.transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class Transaction {

    private final String transactionId;
    private final String accountNumber;
    private final TransactionType transactionType;
    private final BigDecimal amount;
    private final LocalDateTime timestamp;

    public Transaction(
            String transactionId,
            String accountNumber,
            TransactionType transactionType,
            BigDecimal amount,
            LocalDateTime timestamp) {


        Objects.requireNonNull(transactionId, "transactionId cannot be null");
        Objects.requireNonNull(accountNumber, "accountNumber cannot be null");
        Objects.requireNonNull(transactionType, "transactionType cannot be null");
        Objects.requireNonNull(amount, "amount cannot be null");
        Objects.requireNonNull(timestamp, "timestamp cannot be null");

        if (transactionId.isBlank()) {
            throw new IllegalArgumentException(
                    "transactionId cannot be empty");
        }

        if (accountNumber.isBlank()) {
            throw new IllegalArgumentException(
                    "accountNumber cannot be empty");
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "amount cannot be less than or equal to 0");
        }

        this.transactionId = transactionId;
        this.accountNumber = accountNumber;
        this.transactionType = transactionType;
        this.amount = amount;
        this.timestamp = timestamp;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
