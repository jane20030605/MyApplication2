package com.example.myapplication.ui.user_data;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentUserDataBinding;

import java.util.Calendar;

public class user_dataFragment extends Fragment {

    private FragmentUserDataBinding binding;
    private UserUpdateClient userUpdateClient;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUserDataBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // 初始化按鈕元素
        Button buttonEmergencyContact = binding.buttonEmergencyContact;
        Button buttonCancel = binding.buttonCancel;
        Button buttonSave = binding.buttonDataSave;

        userUpdateClient = new UserUpdateClient(); // 初始化UserUpdateClient

        // 點擊保存按钮事件
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 獲取用戶輸入數據
                String username = binding.editTextUsername.getText().toString();
                String email = binding.editTextEmail.getText().toString();
                String phone = binding.editTextPhone.getText().toString();
                String address = binding.editTextAddress.getText().toString();
                String birthday = binding.editTextBirthday.getText().toString();

                // 確保所有字段都已填寫
                if (username.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty() || birthday.isEmpty()) {
                    Toast.makeText(requireContext(), "請填寫所有資料", Toast.LENGTH_SHORT).show();
                    return;
                }
                // 檢查郵件地址格式是否正確
                if (!isValidEmail(email)) {
                    Toast.makeText(requireContext(), "郵件地址格式不正確", Toast.LENGTH_SHORT).show();
                    return;
                }
                // 保存數據到SharedPreferences
                saveUserData(username, email, phone, address, birthday);

                // 發送用戶數據到後端API進行更新
                userUpdateClient.updateUser(username, email, phone, address, birthday, "qwe", new UserUpdateClient.UserUpdateCallback() {
                    @Override
                    public void onSuccess(String message) {
                        requireActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                            }
                        });
                        // 更新成功後的處理
                    }

                    @Override
                    public void onError(String message) {
                        requireActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                            }
                        });
                        // 更新失敗後的處理
                    }
                });

                // 返回上一個介面
                Navigation.findNavController(v).navigateUp();
            }
        });

        // 點擊緊急連絡人按鈕事件
        buttonEmergencyContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.nav_emergency_contact);
            }
        });

        // 點擊取消按钮事件
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 提示進行帳戶密碼變更修改
                Toast.makeText(requireContext(), "進行變更密碼", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(v).navigate(R.id.nav_password_change);
            }
        });

        // 在這裡添加設置EditText的輸入監聽器的代碼
        binding.editTextUsername.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    // 當按下Enter/下一步鍵時，將焦點轉移到下一個EditText
                    binding.editTextEmail.requestFocus();
                    return true;
                }
                return false;
            }
        });

        // 設置生日選擇器
        binding.editTextBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        // 加載用戶數據
        loadUserData();

        return root;
    }

    // 檢查郵件地址格式是否正確的方法
    private boolean isValidEmail(String email) {
        // 使用正則表達式來檢查郵件地址格式
        // 這個正則表達式僅供參考，實際使用中可以根據需求進行修改
        String emailPattern = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}";
        return email.matches(emailPattern);
    }

    // 保存用戶數據到SharedPreferences
    private void saveUserData(String username, String email, String phone, String address, String birthday) {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("REAL NAME", username);
        editor.putString("EMAIL", email);
        editor.putString("PHONE", phone);
        editor.putString("ADDRESS", address);
        editor.putString("BIRTHDAY", birthday);
        editor.apply();
    }

    // 加載用戶數據到UI中
    private void loadUserData() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        String real_name = sharedPreferences.getString("REAL NAME", "");
        String email = sharedPreferences.getString("EMAIL", "");
        String phone = sharedPreferences.getString("PHONE", "");
        String address = sharedPreferences.getString("ADDRESS", "");
        String birthday = sharedPreferences.getString("BIRTHDAY", "");

        binding.editTextUsername.setText(real_name);
        binding.editTextEmail.setText(email);
        binding.editTextPhone.setText(phone);
        binding.editTextAddress.setText(address);
        binding.editTextBirthday.setText(birthday);
    }

    // 顯示日期選擇器
    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        // 將年、月、日組合成日期字串，這裡假設您希望日期格式為YYYY/MM/DD
                        @SuppressLint("DefaultLocale")
                        String formattedDate = String.format("%04d/%02d/%02d", year, (monthOfYear + 1), dayOfMonth);
                        binding.editTextBirthday.setText(formattedDate);
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

}
