package com.example.myapplication.ui.Memory;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.databinding.FragmentMemoryBinding;


public class MemoryFragment extends Fragment {

    private com.example.myapplication.databinding.FragmentMemoryBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        MemoryViewModel medicineboxViewModel =
                new ViewModelProvider(this).get(MemoryViewModel.class);

        binding = FragmentMemoryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textMemory;
        medicineboxViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}