package com.bankcore.service;

import com.bankcore.transaction.Transaction;
import com.bankcore.transaction.TransactionType;

import java.math.BigDecimal;
import java.util.List;

public interface TransactionService {

    Transaction createTransaction(String accountNumber,
                                  TransactionType transactionType,
                                  BigDecimal amount);

    void recordTransaction(Transaction transaction);

    List<Transaction> getTransactions(String accountNumber);
}
