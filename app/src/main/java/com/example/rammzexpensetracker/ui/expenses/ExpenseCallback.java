package com.example.rammzexpensetracker.ui.expenses;

public interface ExpenseCallback {
    Expense OnExpenseLoaded(Expense expense);
    void OnExpenseLoadFailed(String errorMessage);
}
