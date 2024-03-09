package com.example.myapplication.ui.Medicine_box;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.databinding.FragmentMedicineBoxBinding;


public class Medicine_boxFragment extends Fragment {

    private FragmentMedicineBoxBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Medicine_boxViewModel medicineboxViewModel =
                new ViewModelProvider(this).get(Medicine_boxViewModel.class);

        binding = FragmentMedicineBoxBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textMedicineBox;
        medicineboxViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}