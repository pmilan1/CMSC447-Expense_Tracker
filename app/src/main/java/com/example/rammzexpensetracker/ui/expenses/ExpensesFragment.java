package com.example.rammzexpensetracker.ui.expenses;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
//import android.widget.TextView;

import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.example.rammzexpensetracker.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

//import androidx.annotation.NonNull;
//import androidx.fragment.app.Fragment;
//import androidx.lifecycle.ViewModelProvider;

import com.example.rammzexpensetracker.databinding.FragmentExpensesBinding;

public class ExpensesFragment extends Fragment {
    /*
    private FragmentExpensesBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ExpensesViewModel dashboardViewModel =
                new ViewModelProvider(this).get(ExpensesViewModel.class);

        binding = FragmentExpensesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textExpenses;
        dashboardViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
     */

    private EditText dateEditText;
    private EditText descriptionEditText;
    private EditText amountEditText;
    private Button addButton;

    private DatabaseReference databaseReference;

    public ExpensesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_expenses, container, false);

        dateEditText = view.findViewById(R.id.dateEditText);
        descriptionEditText = view.findViewById(R.id.descriptionEditText);
        amountEditText = view.findViewById(R.id.amountEditText);
        addButton = view.findViewById(R.id.addButton);

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("expenses");

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Capture user input
                String date = dateEditText.getText().toString();
                String description = descriptionEditText.getText().toString();
                double amount = Double.parseDouble(amountEditText.getText().toString());

                // Create an Expense object
                Expense expense = new Expense(date, description, amount);

                // Push the expense to Firebase
                String key = databaseReference.push().getKey();
                databaseReference.child(key).setValue(expense);
            }
        });
        // Add code to listen for changes in database and update UI

        return view;
    }
}