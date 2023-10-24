package com.example.rammzexpensetracker.ui.expenses;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.util.Log;

import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.example.rammzexpensetracker.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

//import com.example.rammzexpensetracker.databinding.FragmentExpensesBinding;

public class ExpensesFragment extends Fragment {
    private final ExpenseCallback callback = new ExpenseCallback() {
        // used to get an expense when it is found
        @Override
        public Expense OnExpenseLoaded(Expense expense) {
            Log.d("Expense Loaded", "Expense details: " + expense.getLocation());
            return expense;
        }

        // used when expense is not found
        @Override
        public void OnExpenseLoadFailed(String errorMessage) {
            Log.d("Expense Not Loaded", "Expense was not loaded");
        }
    };


    private EditText locationEditText;
    private EditText dateEditText;
    private EditText descriptionEditText;
    private EditText amountEditText;
    private Button addButton;
    private Button deleteButton;

    private DatabaseReference databaseReference;

    public ExpensesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_expenses, container, false);
        ExpenseCRUD crud = new ExpenseCRUD();

        locationEditText = view.findViewById(R.id.locationEditText);
        dateEditText = view.findViewById(R.id.dateEditText);
        descriptionEditText = view.findViewById(R.id.descriptionEditText);
        amountEditText = view.findViewById(R.id.amountEditText);
        addButton = view.findViewById(R.id.addButton);
        deleteButton = view.findViewById(R.id.deleteButton);

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("expenses");

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Capture user input
                String location = locationEditText.getText().toString();
                String date = dateEditText.getText().toString();
                String description = descriptionEditText.getText().toString();
                double amount = Double.parseDouble(amountEditText.getText().toString());

            /*
                // Create an Expense object
                Expense expense = new Expense(location, date, description, amount);

                // Push the expense to Firebase
                String key = databaseReference.push().getKey();
                databaseReference.child(key).setValue(expense);
            */

                // adds expense to database
                crud.AddExpense(databaseReference, location, date, description, amount);
                crud.GetExpense(databaseReference, "1234", callback);
                //crud.EditExpense(databaseReference, "1234", "Wendy's", "10/25/23", "mmmm", 10.00);
                //crud.GetExpense(databaseReference, "1234", callback);
                // Inside the onSuccess callback of DeleteExpense, check if the expense was deleted



                //crud.EditExpense(databaseReference, "1234", "Wendy's", "10/25/23", "mmmm", 10.00);
                //crud.GetExpense(databaseReference, "1234", callback);


                // Clear the text in the boxes
                locationEditText.setText("");
                dateEditText.setText("");
                descriptionEditText.setText("");
                amountEditText.setText("");
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crud.GetExpense(databaseReference, "1234", callback);
                crud.DeleteExpense(databaseReference, "1234");
                crud.GetExpense(databaseReference, "1234", callback);




                // Clear the text in the boxes
                locationEditText.setText("");
                dateEditText.setText("");
                descriptionEditText.setText("");
                amountEditText.setText("");
            }
        });
        return view;
    }
}