package com.example.myapplication.ui.emergency;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
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

        // 初始化視圖元素
        EditText editEmergencyName = binding.editEmergencyName;
        EditText editTextPhone = binding.editTextPhone;
        Spinner spinnerContact = binding.spinnerContact;
        Button buttonSave = binding.buttonSave;
        Button buttonCancel = binding.buttonCancel;

        // 初始化 ViewModel
        mViewModel = new ViewModelProvider(requireActivity()).get(EmergencyContactViewModel.class);

        // 從 ViewModel 中獲取緊急聯絡人信息並填充到視圖中
        //獲取聯絡人姓名
        mViewModel.getEmergencyName().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String emergencyName) {
                editEmergencyName.setText(emergencyName);
            }
        });
        //獲取聯絡人電話
        mViewModel.getPhoneNumber().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String phoneNumber) {
                editTextPhone.setText(phoneNumber);
            }
        });
        //獲取聯絡人關係
        mViewModel.getRelationship().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String relationship) {
                // 選擇 spinner 中的相應項目
                int position = getPositionByValue(spinnerContact, relationship);
                spinnerContact.setSelection(position);
            }
        });

        // 設置下拉選單項目
        setSpinnerItems(spinnerContact, R.array.contact);

        // 儲存按鈕的點擊事件
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 獲取用戶輸入的緊急聯絡人信息
                String emergencyName = editEmergencyName.getText().toString().trim();
                String phoneNumber = editTextPhone.getText().toString().trim();
                String relationship = spinnerContact.getSelectedItem().toString();

                // 確保所有字段都已填寫
                if (emergencyName.isEmpty() || phoneNumber.isEmpty()) {
                    Toast.makeText(requireContext(), "請填寫所有資料", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 將緊急聯絡人信息保存到 ViewModel 中
                mViewModel.setEmergencyContact(emergencyName, phoneNumber, relationship);

                Toast.makeText(requireContext(), "緊急聯絡人已保存", Toast.LENGTH_SHORT).show();

                // 返回上一個畫面
                Navigation.findNavController(v).navigateUp();
            }
        });

        // 取消按鈕的點擊事件
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(requireContext(), "取消新增緊急連絡人", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(v).navigateUp();
            }
        });

        // 在這裡添加設置EditText的輸入監聽器的代碼
        editEmergencyName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    // 當按下Enter/下一步鍵時，將焦點轉移到下一個EditText
                    editTextPhone.requestFocus();
                    return true;
                }
                return false;
            }
        });

        return root;
    }

    // 設置下拉選單項目
    private void setSpinnerItems(Spinner spinner, int arrayId) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(),
                arrayId, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    // 根據值獲取 spinner 中對應的位置
    private int getPositionByValue(Spinner spinner, String value) {
        ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) spinner.getAdapter();
        return adapter.getPosition(value);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
