package com.example.rammzexpensetracker.ui.expenses;

public class Expense {
    String key, date, amount, category;

    // Empty constructor needed for Firebase
    public Expense() {}

    public String getDate() {
        return date;
    }

    public String getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }

    public String getKey() {
        return key;
    }

    public void setTitle(String date) {
        this.date = date;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setKey(String key) {
        this.key = key;
    }
}