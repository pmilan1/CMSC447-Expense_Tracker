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
        View view = inflater.inflate(R.layout.fragment_expenses, container, false);
        FirebaseApp.initializeApp(requireActivity());
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        FloatingActionButton add = view.findViewById(R.id.addExpense);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View view1 = LayoutInflater.from(requireActivity()).inflate(R.layout.add_expense_dialog, null);
                TextInputLayout dateLayout, amountLayout, categoryLayout;
                dateLayout = view1.findViewById(R.id.dateLayout);
                amountLayout = view1.findViewById(R.id.amountLayout);
                categoryLayout = view1.findViewById(R.id.categoryLayout);
                TextInputEditText dateET, amountET, categoryET;
                dateET = view1.findViewById(R.id.dateET);
                amountET = view1.findViewById(R.id.amountET);
                categoryET = view1.findViewById(R.id.categoryET);
                AlertDialog alertDialog = new AlertDialog.Builder(requireActivity())
                        .setTitle("Add")
                        .setView(view1)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (Objects.requireNonNull(dateET.getText()).toString().isEmpty()) {
                                    dateLayout.setError("This field is required!");
                                } else if (Objects.requireNonNull(amountET.getText()).toString().isEmpty()) {
                                    amountLayout.setError("This field is required!");
                                } else if (Objects.requireNonNull(categoryET.getText()).toString().isEmpty()) {
                                    categoryLayout.setError("This field is required!");
                                } else {
                                    ProgressDialog dialog = new ProgressDialog(requireActivity());
                                    dialog.setMessage("Storing in Database...");
                                    dialog.show();
                                    Expense expense = new Expense();
                                    expense.setTitle(dateET.getText().toString());
                                    expense.setAmount(amountET.getText().toString());
                                    expense.setCategory(categoryET.getText().toString());
                                    database.getReference().child("expenses").push().setValue(expense).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            dialog.dismiss();
                                            dialogInterface.dismiss();
                                            Toast.makeText(requireActivity(), "Saved Successfully!", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            dialog.dismiss();
                                            Toast.makeText(requireActivity(), "There was an error while saving data", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .create();
                alertDialog.show();
            }
        });

        TextView empty = view.findViewById(R.id.empty);

        RecyclerView recyclerView = view.findViewById(R.id.recycler);

        database.getReference().child("expenses").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Expense> arrayList = new ArrayList<>();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    Expense expense = dataSnapshot.getValue(Expense.class);
                    Objects.requireNonNull(expense).setKey(dataSnapshot.getKey());
                    arrayList.add(expense);
                }

                if (arrayList.isEmpty()) {
                    empty.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    empty.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }

                ExpenseAdapter adapter = new ExpenseAdapter(requireActivity(), arrayList);
                recyclerView.setAdapter(adapter);

                adapter.setOnItemClickListener(new ExpenseAdapter.OnItemClickListener() {
                    @Override
                    public void onClick(Expense expense) {
                        View view = LayoutInflater.from(requireActivity()).inflate(R.layout.add_expense_dialog, null);
                        TextInputLayout dateLayout, amountLayout, categoryLayout;
                        TextInputEditText dateET, amountET, categoryET;

                        dateET = view.findViewById(R.id.dateET);
                        amountET = view.findViewById(R.id.amountET);
                        categoryET = view.findViewById(R.id.categoryET);
                        dateLayout = view.findViewById(R.id.dateLayout);
                        amountLayout = view.findViewById(R.id.amountLayout);
                        categoryLayout = view.findViewById(R.id.categoryLayout);

                        dateET.setText(expense.getDate());
                        amountET.setText(expense.getAmount());
                        categoryET.setText(expense.getCategory());

                        ProgressDialog progressDialog = new ProgressDialog(requireActivity());

                        AlertDialog alertDialog = new AlertDialog.Builder(requireActivity())
                                .setTitle("Edit")
                                .setView(view)
                                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        if (Objects.requireNonNull(dateET.getText()).toString().isEmpty()) {
                                            dateLayout.setError("This field is required!");
                                        } else if (Objects.requireNonNull(amountET.getText()).toString().isEmpty()) {
                                            amountLayout.setError("This field is required!");
                                        } else if (Objects.requireNonNull(categoryET.getText()).toString().isEmpty()) {
                                            categoryLayout.setError("This field is required!");
                                        } else {
                                            progressDialog.setMessage("Saving...");
                                            progressDialog.show();
                                            Expense expense1 = new Expense();
                                            expense1.setTitle(dateET.getText().toString());
                                            expense1.setAmount(amountET.getText().toString());
                                            expense1.setCategory(categoryET.getText().toString());
                                            database.getReference().child("expenses").child(expense.getKey()).setValue(expense1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    progressDialog.dismiss();
                                                    dialogInterface.dismiss();
                                                    Toast.makeText(requireActivity(), "Saved Successfully!", Toast.LENGTH_SHORT).show();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(requireActivity(), "There was an error while saving data", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    }
                                })
                                .setNeutralButton("Close", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                })
                                .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        progressDialog.setTitle("Deleting...");
                                        progressDialog.show();
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

            }
        });
        return view;
    }
}