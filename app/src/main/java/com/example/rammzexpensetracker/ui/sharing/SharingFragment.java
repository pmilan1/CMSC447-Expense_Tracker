package com.example.rammzexpensetracker.ui.sharing;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.rammzexpensetracker.databinding.FragmentSharingBinding;

public class SharingFragment extends Fragment {

    private FragmentSharingBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SharingViewModel dashboardViewModel =
                new ViewModelProvider(this).get(SharingViewModel.class);

        binding = FragmentSharingBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textSharing;
        dashboardViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}