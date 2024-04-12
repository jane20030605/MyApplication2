package com.example.myapplication.ui.emergency;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentEmergencyContactBinding;

public class emergency_contact extends Fragment {

    private FragmentEmergencyContactBinding binding;
    private EmergencyContactViewModel mViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentEmergencyContactBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // 初始化視圖
        // 緊急聯絡人姓名輸入框
        EditText editEmergencyName = binding.editEmergencyName;
        // 電話號碼輸入框
        EditText editTextPhone = binding.editTextPhone;
        // 關係下拉選單
        Spinner spinnerContact = binding.spinnerContact;
        // 儲存按鈕
        Button buttonSave = binding.buttonSave;
        // 取消按鈕
        Button buttonCancel = binding.buttonCancel;

        setSpinnerItems(spinnerContact, R.array.contact);

        mViewModel = new ViewModelProvider(requireActivity()).get(EmergencyContactViewModel.class);

        // 設置儲存按鈕點擊監聽器
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 獲取用戶輸入的緊急聯絡人信息
                String emergencyName = editEmergencyName.getText().toString().trim();
                String phoneNumber = editTextPhone.getText().toString().trim();
                String relationship = spinnerContact.getSelectedItem().toString();

                // 確保所有字段都已填寫
                if (emergencyName.isEmpty() || phoneNumber.isEmpty()) {
                    Toast.makeText(requireContext(), "請填寫所有字段", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 將緊急聯絡人信息保存到ViewModel中
                mViewModel.setEmergencyContact(emergencyName, phoneNumber, relationship);

                Toast.makeText(requireContext(), "緊急聯絡人已保存", Toast.LENGTH_SHORT).show();

                // 返回上一個畫面
                Navigation.findNavController(v).navigateUp();
            }
        });

        // 設置取消按鈕點擊監聽器
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(requireContext(), "取消新增緊急連絡人", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(v).navigate(R.id.nav_user_data);
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setSpinnerItems(Spinner spinner, int arrayId) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(),
                arrayId, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }
}
