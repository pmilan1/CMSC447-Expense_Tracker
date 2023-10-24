package com.example.rammzexpensetracker.ui.expenses;

import androidx.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class ExpenseCRUD {
    void AddExpense(DatabaseReference db, String location, String date, String description, double amount) {
        // creates a new expense and adds it to the database
        Expense newExpense = new Expense(location, date, description, amount);
        // Push the new expense to Firebase
        //String key = db.push().getKey();
        String key = "1234";
        db.child("expenses").child(key).setValue(newExpense);
    }

    void GetExpense(DatabaseReference db, String key, ExpenseCallback callback) {
        db.child("expenses").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Expense expense = snapshot.getValue(Expense.class);
                    callback.OnExpenseLoaded(expense);
                } else {
                    callback.OnExpenseLoadFailed("Expense not found!");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.OnExpenseLoadFailed(error.getMessage());
            }
        });
    }

    void DeleteExpense(DatabaseReference db, String key) {

        // removes expense from table
        db.child(key).removeValue()
            .addOnSuccessListener(unused -> Log.d("DeleteExpense", "Successfully deleted expense"))
            .addOnFailureListener(e -> Log.e("DeleteExpense", "Failed to delete expense" + e.getMessage()));
    }

    void EditExpense(DatabaseReference db, String key, String newLocation, String newDate, String newDescription, double newAmount) {
        Map<String, Object> updates = new HashMap<>();

        // sets all attributes to new values
        updates.put("location", newLocation);
        updates.put("date", newDate);
        updates.put("description", newDescription);
        updates.put("amount", newAmount);
        DatabaseReference expenseRef = db.child(key);
        expenseRef.updateChildren(updates)
            .addOnSuccessListener(unused -> Log.d("EditExpense", "Expense updated successfully"))
            .addOnFailureListener(e -> Log.e("EditExpense", "Failed to update expense: " + e.getMessage()));
    }
}
