package com.pos.model;

import java.sql.Date;

public class Expense {

    private int expenseId;
    private String title;
    private String category;
    private double amount;
    private Date expenseDate;
    private String recordedBy;

    public Expense(int expenseId, String title, String category,
                   double amount, Date expenseDate, String recordedBy) {
        this.expenseId = expenseId;
        this.title = title;
        this.category = category;
        this.amount = amount;
        this.expenseDate = expenseDate;
        this.recordedBy = recordedBy;
    }

    public int getExpenseId() {
        return expenseId;
    }

    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }

    public double getAmount() {
        return amount;
    }

    public Date getExpenseDate() {
        return expenseDate;
    }

    public String getRecordedBy() {
        return recordedBy;
    }
}