package com.example.rammzexpensetracker.ui.expenses;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.rammzexpensetracker.R;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class ExpensesFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Create view that is attached to fragment_expenses.xml
        View view = inflater.inflate(R.layout.fragment_expenses, container, false);

        // Initialize Firebase Realtime Database
        FirebaseApp.initializeApp(requireActivity());
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        // Sets onClick action for addExpense button
        FloatingActionButton add = view.findViewById(R.id.addExpense);  // attach action to button
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {    // action when button is clicked
                // Attaches view to use add_expense_dialog.xml
                View view1 = LayoutInflater.from(requireActivity()).inflate(R.layout.add_expense_dialog, null);

                // Initialize variables with each TextInput and TextEdit widgets
                TextInputLayout dateLayout, amountLayout, categoryLayout;
                dateLayout = view1.findViewById(R.id.dateLayout);
                amountLayout = view1.findViewById(R.id.amountLayout);
                categoryLayout = view1.findViewById(R.id.categoryLayout);
                TextInputEditText dateET, amountET, categoryET;
                dateET = view1.findViewById(R.id.dateET);
                amountET = view1.findViewById(R.id.amountET);
                categoryET = view1.findViewById(R.id.categoryET);

                // Creates an floating box
                // Used to collect expense information from the user
                AlertDialog alertDialog = new AlertDialog.Builder(requireActivity())
                        .setTitle("Add")
                        .setView(view1)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                // Validates input fields and returns an error if empty
                                if (Objects.requireNonNull(dateET.getText()).toString().isEmpty()) {
                                    dateLayout.setError("This field is required!");
                                }
                                else if (Objects.requireNonNull(amountET.getText()).toString().isEmpty()) {
                                    amountLayout.setError("This field is required!");
                                }
                                else if (Objects.requireNonNull(categoryET.getText()).toString().isEmpty()) {
                                    categoryLayout.setError("This field is required!");
                                }
                                else {  // Shows a progress dialog when expense is being saved
                                    ProgressDialog dialog = new ProgressDialog(requireActivity());
                                    dialog.setMessage("Storing in Database...");
                                    dialog.show();

                                    Expense expense = new Expense();    // create expense item

                                    // use setter functions to store user input
                                    expense.setTitle(dateET.getText().toString());
                                    expense.setAmount(amountET.getText().toString());
                                    expense.setCategory(categoryET.getText().toString());

                                    // Pushes data to the 'expenses' node in Firebase database
                                    database.getReference().child("expenses").push().setValue(expense).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            dialog.dismiss();   // removes floating dialog from screen
                                            dialogInterface.dismiss();
                                            Toast.makeText(requireActivity(), "Saved Successfully!", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {   // if unsuccessful, throw error
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            dialog.dismiss();   // removes floating dialog from screen
                                            Toast.makeText(requireActivity(), "There was an error while saving data", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        })
                        // Cancel action button
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();  // remove floating dialog if cancel is selected
                            }
                        })
                        .create();
                alertDialog.show();
            }
        });

        TextView empty = view.findViewById(R.id.empty);
        RecyclerView recyclerView = view.findViewById(R.id.recycler);

        // Adds eventListener to 'expenses' node in database
        // Will alert when data is modified or added
        database.getReference().child("expenses").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {  // called when data is changed
                ArrayList<Expense> arrayList = new ArrayList<>();

                // Iterates over Expense object for each child
                // It then adds to an array to locally store data
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    Expense expense = dataSnapshot.getValue(Expense.class);
                    Objects.requireNonNull(expense).setKey(dataSnapshot.getKey());
                    arrayList.add(expense);
                }

                // If no entries are present, no cards will be visible, the screen will be blank
                if (arrayList.isEmpty()) {
                    empty.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
                else {
                    empty.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }

                // Passes array of data to ExpenseAdapter class
                ExpenseAdapter adapter = new ExpenseAdapter(requireActivity(), arrayList);
                recyclerView.setAdapter(adapter);   // sets adapter for RecyclerView

                // Will be triggered when user clicks on RecyclerView object
                adapter.setOnItemClickListener(new ExpenseAdapter.OnItemClickListener() {
                    @Override
                    public void onClick(Expense expense) {
                        // connects to add_expense_data.xml file
                        View view = LayoutInflater.from(requireActivity()).inflate(R.layout.add_expense_dialog, null);

                        // Create variable for each input box (from XML file)
                        TextInputLayout dateLayout, amountLayout, categoryLayout;
                        TextInputEditText dateET, amountET, categoryET;

                        // Set input data to variable
                        dateET = view.findViewById(R.id.dateET);
                        amountET = view.findViewById(R.id.amountET);
                        categoryET = view.findViewById(R.id.categoryET);
                        dateLayout = view.findViewById(R.id.dateLayout);
                        amountLayout = view.findViewById(R.id.amountLayout);
                        categoryLayout = view.findViewById(R.id.categoryLayout);

                        // Uses setters to store data
                        dateET.setText(expense.getDate());
                        amountET.setText(expense.getAmount());
                        categoryET.setText(expense.getCategory());

                        ProgressDialog progressDialog = new ProgressDialog(requireActivity());

                        AlertDialog alertDialog = new AlertDialog.Builder(requireActivity())
                                .setTitle("Edit")
                                .setView(view)

                                // Will be triggered when user clicks "Save" button
                                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        // Validates user input for empty fields, returns error if empty
                                        if (Objects.requireNonNull(dateET.getText()).toString().isEmpty()) {
                                            dateLayout.setError("This field is required!");
                                        }
                                        else if (Objects.requireNonNull(amountET.getText()).toString().isEmpty()) {
                                            amountLayout.setError("This field is required!");
                                        }
                                        else if (Objects.requireNonNull(categoryET.getText()).toString().isEmpty()) {
                                            categoryLayout.setError("This field is required!");
                                        }
                                        else {
                                            // Shows progress while expense is being saved
                                            progressDialog.setMessage("Saving...");
                                            progressDialog.show();

                                            Expense expense1 = new Expense();   // creates Expense object

                                            // populates Expense object with data
                                            expense1.setTitle(dateET.getText().toString());
                                            expense1.setAmount(amountET.getText().toString());
                                            expense1.setCategory(categoryET.getText().toString());

                                            // Overwrites data currently stored in database with new data entered by user
                                            database.getReference().child("expenses").child(expense.getKey()).setValue(expense1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    // Dismisses progress and dialog box
                                                    progressDialog.dismiss();
                                                    dialogInterface.dismiss();

                                                    // Shows toast message
                                                    Toast.makeText(requireActivity(), "Saved Successfully!", Toast.LENGTH_SHORT).show();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    progressDialog.dismiss();   // dismisses progress screen

                                                    // Shows error toast message
                                                    Toast.makeText(requireActivity(), "There was an error while saving data", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    }
                                })
                                // Allows the user to exit dialog box without any changes
                                .setNeutralButton("Close", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                })
                                // Sets delete action to button
                                .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        progressDialog.setTitle("Deleting...");
                                        progressDialog.show();

                                        // Deletes item from Firebase database
                                        database.getReference().child("expenses").child(expense.getKey()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                progressDialog.dismiss();
                                                Toast.makeText(requireActivity(), "Deleted Successfully", Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressDialog.dismiss();
                                            }
                                        });
                                    }
                                }).create();
                        alertDialog.show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Do nothing. Required function.
            }
        });
        return view;
    }
}