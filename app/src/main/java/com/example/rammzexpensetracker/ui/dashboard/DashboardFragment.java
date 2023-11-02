package com.example.rammzexpensetracker.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.example.rammzexpensetracker.ui.expenses.Expense;

import com.example.rammzexpensetracker.databinding.FragmentDashboardBinding;
import com.google.firebase.database.ValueEventListener;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        Budget budget = new Budget();

        // Initialize Firebase Realtime Database
        FirebaseApp.initializeApp(requireActivity());
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference expenseRef = database.getReference().child("expenses");
        DatabaseReference budgetRef = database.getReference().child("budget");
        expenseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                double expenseTotal = 0;

                // loops through each expense and adds up the total expense.
                for (DataSnapshot expenseSnapshot : snapshot.getChildren()) {
                    Expense expense = expenseSnapshot.getValue(Expense.class);
                    if (expense != null) {
                        expenseTotal += Double.parseDouble(expense.getAmount());
                    }
                }
                budget.setBudget(100.00);
                budget.setTotalExpenses(expenseTotal);
                System.out.println("Expense Total: " + budget.getTotalExpenses());
                budgetRef.setValue(budget, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        if (databaseError == null) {
                            System.out.println("Budget added successfully");
                        } else {
                            System.out.println("Budget failed to be added!");
                        }
                    }
                });
                if (budget.getTotalExpenses() >= budget.getBudget()) {
                    System.out.println("Your Budget has been maxed out!");
                } else {
                    System.out.println("Keep spending you dumb idiot. You'll max out eventually!");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textDashboard;
        dashboardViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}