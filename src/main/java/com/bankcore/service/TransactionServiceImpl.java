package com.bankcore.service;

import com.bankcore.transaction.Transaction;
import com.bankcore.transaction.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TransactionServiceImpl implements TransactionService {

    private long transactionSequence = 1;
    private final List<Transaction> transactions = new ArrayList<>();

    @Override
    public Transaction createTransaction(String accountNumber, TransactionType transactionType, BigDecimal amount) {
        return new Transaction(
                generateTransactionId(),
                accountNumber,
                transactionType,
                amount,
                LocalDateTime.now());
    }

    @Override
    public void recordTransaction(Transaction transaction) {
        if(transaction == null) {
            throw new NullPointerException("Transaction is null.");
        }
        transactions.add(transaction);
    }

    @Override
    public List<Transaction> getTransactions(String accountNumber) {

        return transactions.stream()
                .filter(t -> t.getAccountNumber().equalsIgnoreCase(accountNumber))
                .toList();
    }

    private String generateTransactionId() {
        return "TXN" + transactionSequence++;
    }
}
