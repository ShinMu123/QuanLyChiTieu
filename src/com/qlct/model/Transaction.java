package com.qlct.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Transaction {
    private int transId;
    private int userId;
    private Category category;
    private BigDecimal amount;
    private LocalDate transactionDate;
    private String note;
    private boolean deleted;

    public Transaction() {
    }

    public Transaction(int transId, int userId, Category category, BigDecimal amount, LocalDate transactionDate,
            String note, boolean deleted) {
        this.transId = transId;
        this.userId = userId;
        this.category = category;
        this.amount = amount;
        this.transactionDate = transactionDate;
        this.note = note;
        this.deleted = deleted;
    }

    public int getTransId() {
        return transId;
    }

    public void setTransId(int transId) {
        this.transId = transId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}








