package com.example.myapplication.ui.user_data;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentUserDataBinding;
import com.example.myapplication.utils.NetworkRequestManager;
import com.example.myapplication.utils.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class UserDataFragment extends Fragment {

    private FragmentUserDataBinding binding;
    private UserUpdateClient userUpdateClient;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentUserDataBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // 初始化按鈕元素
        Button buttonEmergencyContact = binding.buttonEmergencyContact;
        Button buttonCancel = binding.buttonCancel;
        Button buttonSave = binding.buttonDataSave;
        Button buttonContact = binding.buttonContact;

        userUpdateClient = new UserUpdateClient(); // 初始化UserUpdateClient
        sessionManager = new SessionManager(requireContext()); // 初始化SessionManager

        // 加載用戶數據
        if (sessionManager.isLoggedIn()) {
            String account = sessionManager.getCurrentLoggedInAccount();
            Log.d("UserDataFragment", "正在從後端獲取用戶數據，帳戶：" + account);
            fetchUserData(account);
        } else {
            Log.d("UserDataFragment", "用戶未登錄，導航到登錄介面");
            navigateToLogin();
        }

        // 點擊保存按钮事件
        buttonSave.setOnClickListener(v -> {
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
            userUpdateClient.updateUser(username, email, phone, address, birthday, sessionManager.getCurrentLoggedInAccount(), new UserUpdateClient.UserUpdateCallback() {
                @Override
                public void onSuccess(String message) {
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(requireContext(), "用戶數據更新成功：" + message, Toast.LENGTH_SHORT).show();
                        Log.d("UserDataFragment", "用戶數據更新成功：" + message);
                        // 更新用戶數據成功後，返回上一個介面
                        Navigation.findNavController(v).navigateUp();
                    });
                }

                @Override
                public void onError(String message) {
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(requireContext(), "用戶數據更新失敗：" + message, Toast.LENGTH_SHORT).show();
                        Log.d("UserDataFragment", "用戶數據更新失敗：" + message);
                    });
                }
            });
        });

        // 點擊緊急聯絡人按钮事件
        buttonEmergencyContact.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "新增緊急聯絡人", Toast.LENGTH_SHORT).show();
            Navigation.findNavController(v).navigate(R.id.nav_emergency_contact);
        });

        // 點擊取消按钮事件
        buttonCancel.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "進行密碼更改", Toast.LENGTH_SHORT).show();
            Navigation.findNavController(v).navigate(R.id.nav_password_change);
        });
        buttonContact.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "緊急連絡人列表", Toast.LENGTH_SHORT).show();
            Navigation.findNavController(v).navigate(R.id.nav_contact_list);
        });


        // 設置EditText的輸入監聽器
        binding.editTextUsername.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                binding.editTextEmail.requestFocus();
                return true;
            }
            return false;
        });

        // 設置生日選擇器
        binding.editTextBirthday.setOnClickListener(v -> showDatePickerDialog());

        return root;
    }

    // 檢查郵件地址格式是否正確的方法
    private boolean isValidEmail(String email) {
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

        Log.d("UserDataFragment", "保存用戶數據到SharedPreferences：姓名：" + username + "，郵件：" + email + "，電話：" + phone +
                "，地址：" + address + "，生日：" + birthday);
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
        Log.d("UserDataFragment", "從SharedPreferences加載用戶數據到UI：姓名：" + real_name + "，郵件：" + email + "，電話：" + phone +
                "，地址：" + address + "，生日：" + birthday);
    }

    // 顯示日期選擇器
    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                (view, year, monthOfYear, dayOfMonth) -> {
                    // 將年、月、日組合成日期字串，這裡假設您希望日期格式為YYYY/MM/DD
                    @SuppressLint("DefaultLocale")
                    String formattedDate = String.format("%04d/%02d/%02d", year, (monthOfYear + 1), dayOfMonth);
                    binding.editTextBirthday.setText(formattedDate);
                    Log.d("UserDataFragment", "選擇的生日日期：" + formattedDate);
                }, mYear, mMonth, mDay);

        datePickerDialog.show();
    }

    // 獲取用戶數據
    private void fetchUserData(String account) {
        String phpApiUrl = "http://100.96.1.3/api_update_userdata.php";

        // 構建POST請求的參數
        Map<String, String> params = new HashMap<>();
        params.put("account", account);

        NetworkRequestManager.getInstance(getContext()).makePostRequest(phpApiUrl, params.toString(), new NetworkRequestManager.RequestListener() {
            @Override
            public void onSuccess(String response) {
                displayUserData(response); // 成功從後端獲取用戶數據
                Log.d("UserDataFragment", "成功從後端獲取用戶數據：" + response);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(requireContext(), "無法從後端獲取用戶數據：" + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 顯示用戶數據
    private void displayUserData(String response) {
        Log.d("UserDataFragment", "收到的用戶數據JSON：" + response);

        try {
            JSONObject responseJson = new JSONObject(response);
            String message = responseJson.getString("message");

            if ("查詢成功".equals(message)) {
                JSONObject dataObject = responseJson.getJSONObject("data");
                String username = dataObject.getString("username");
                String email = dataObject.getString("email");
                String phone = dataObject.getString("phone");
                String address = dataObject.getString("address");
                String birthday = dataObject.getString("birthday");

                requireActivity().runOnUiThread(() -> {
                    binding.editTextUsername.setText(username);
                    binding.editTextEmail.setText(email);
                    binding.editTextPhone.setText(phone);
                    binding.editTextAddress.setText(address);
                    binding.editTextBirthday.setText(birthday);

                    saveUserData(username, email, phone, address, birthday);
                    Log.d("UserDataFragment", "成功顯示用戶數據到UI：姓名：" + username + "，郵件：" + email + "，電話：" + phone +
                            "，地址：" + address + "，生日：" + birthday);
                });
            } else {
                requireActivity().runOnUiThread(() -> {
                    Log.e("UserDataFragment", "從後端獲取的消息不符合預期：" + message);
                    Toast.makeText(requireContext(), "服務器返回了意外的響應", Toast.LENGTH_SHORT).show();
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("UserDataFragment", "解析用戶數據JSON時出錯：" + e.getMessage());
            requireActivity().runOnUiThread(() -> Toast.makeText(requireContext(), "解析用戶數據時出錯", Toast.LENGTH_SHORT).show());
            // 可以設置默認值或其他適當的處理方法
        }
    }

    // 導航到登錄界面
    private void navigateToLogin() {
        Navigation.findNavController(requireView()).navigate(R.id.nav_login);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

