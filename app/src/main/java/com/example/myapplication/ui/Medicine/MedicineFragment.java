package com.example.myapplication.ui.Medicine;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentMedicineBinding;

public class MedicineFragment extends Fragment {

    private FragmentMedicineBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        MedicineViewModel medicineViewModel = new ViewModelProvider(this).get(MedicineViewModel.class);

        binding = FragmentMedicineBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // 布局設計
        final TextView textView = binding.textView;
        final Spinner spinner_licenseNumber = binding.spinnerLicenseNumber;
        final Spinner spinner_shape = binding.spinnerShape;
        final Spinner spinner_notch = binding.spinnerNotch;
        final Spinner spinner_color = binding.spinnerColor;
        final Spinner spinner_symbol = binding.spinnerSymbol;
        final Spinner spinner_markings = binding.spinnerMarkings;

        // 觀察 medicineViewModel 數據變化，並更新內容
        medicineViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        // 設置下拉式選單的項目
        setSpinnerItems(spinner_licenseNumber, R.array.license_numbers);
        setSpinnerItems(spinner_shape, R.array.shapes);
        setSpinnerItems(spinner_notch, R.array.notches);
        setSpinnerItems(spinner_color, R.array.colors);
        setSpinnerItems(spinner_symbol, R.array.symbols);
        setSpinnerItems(spinner_markings, R.array.markings);

        return root;
    }

    private void setSpinnerItems(Spinner spinner, int arrayResource) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(),
                arrayResource, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
