package com.example.rammzexpensetracker.ui.expenses;

public interface ExpenseCallback {
    void OnExpenseLoaded(Expense expense);
    void OnExpenseLoadFailed(String errorMessage);
}
