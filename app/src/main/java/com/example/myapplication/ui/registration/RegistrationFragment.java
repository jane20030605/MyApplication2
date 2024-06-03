package com.example.myapplication.ui.registration;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentRegistrationBinding;
import com.example.myapplication.utils.UserManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;

public class RegistrationFragment extends Fragment {

    // 建立 Retrofit 實例
    private RegistrationApiClient apiClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiClient = new RegistrationApiClient();
    }

    // RegisterUserTask 現在使用 Retrofit 來處理 API 請求
    @SuppressLint("StaticFieldLeak")
    private class RegisterUserTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            try {
                // 從參數中獲取註冊資料
                String enteredUsername = params[0];
                String enteredPassword = params[1];
                String enteredEmail = params[2];
                String enteredRealName = params[3];
                String enteredPhone = params[4];
                String enteredHome = params[5];
                String enteredBirthday = params[6];

                // 創建 RegistrationRequest 實例
                RegistrationRequest request = new RegistrationRequest();
                request.setUsername(enteredUsername);
                request.setPassword(enteredPassword);
                request.setEmail(enteredEmail);
                request.setRealName(enteredRealName);
                request.setPhone(enteredPhone);
                request.setHome(enteredHome);
                request.setBirthday(enteredBirthday);

                // 創建 JSON 物件
                JSONObject requestData = new JSONObject();
                requestData.put("account", enteredUsername);
                requestData.put("password", enteredPassword);
                requestData.put("name", enteredRealName);
                requestData.put("birthday", enteredBirthday);
                requestData.put("tel", enteredPhone);
                requestData.put("address", enteredHome);
                requestData.put("mail", enteredEmail);

                // 使用 Retrofit 來執行 API 請求
                apiClient.registerUser(requestData, new RegistrationApiClient.RegistrationCallback() {
                    @Override
                    public void onSuccess(String message) {
                        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                        if (message.equals("註冊成功")) {
                            // 註冊成功後導航到下一個 Fragment 或 Activity
                            Navigation.findNavController(requireView()).navigate(R.id.nav_login);
                        }
                    }

                    @Override
                    public void onError(String message) {
                        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // 創建RegistrationViewModel實例以便後續使用
        RegistrationViewModel registrationViewModel =
                new ViewModelProvider(this).get(RegistrationViewModel.class);

        // 綁定Fragment的視圖與相關的資源
        FragmentRegistrationBinding binding = FragmentRegistrationBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // 獲取輸入欄位及按鈕
        final EditText username = binding.username;
        final EditText password = binding.password;
        final EditText passwordCheck = binding.passwordCheck;
        final EditText realName = binding.realName;
        final EditText phone = binding.phone;
        final EditText email = binding.email;
        final EditText home = binding.home;
        final TextView birthday = binding.birthday;
        final Button registerButton = binding.registerButton;

        // 設定生日選擇事件
        birthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int mYear = calendar.get(Calendar.YEAR);
                int mMonth = calendar.get(Calendar.MONTH);
                int mDay = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                @SuppressLint("DefaultLocale")
                                String formattedDate = String.format
                                        ("%04d/%02d/%02d", year, (monthOfYear + 1), dayOfMonth);
                                birthday.setText(formattedDate);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        // 設定電話號碼格式
        phone.addTextChangedListener(new TextWatcher() {
            private boolean isFormatting;
            private int after;
            private static final int TOTAL_SYMBOLS = 12;
            private static final int TOTAL_DIGITS = 10;
            private static final int FIRST_DIVIDER_POSITION = 5;
            private static final int SECOND_DIVIDER_POSITION = 9;
            private static final char DIVIDER = '-';

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                this.after = after;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0 && !isFormatting) {
                    isFormatting = true;
                    if (after > 0 && (s.length() == FIRST_DIVIDER_POSITION || s.length() == SECOND_DIVIDER_POSITION)) {
                        if (s.length() < TOTAL_SYMBOLS)
                        {
                            phone.setText(new StringBuilder(s).insert(s.length() - 1, DIVIDER).toString());
                            phone.setSelection(phone.getText().length());
                        }
                    } else if (after == 0 && s.length() > 0 && (s.length() == FIRST_DIVIDER_POSITION || s.length() == SECOND_DIVIDER_POSITION)) {
                        phone.setText(s.subSequence(0, s.length() - 1));
                        phone.setSelection(phone.getText().length());
                    }
                    isFormatting = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > TOTAL_SYMBOLS) {
                    s.delete(TOTAL_SYMBOLS, s.length());
                }
            }
        });

        // 設定註冊按鈕點擊事件
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 獲取用戶輸入的資料
                String enteredUsername = username.getText().toString().trim();
                String enteredPassword = password.getText().toString().trim();
                String enteredPasswordCheck = passwordCheck.getText().toString().trim();
                String enteredRealName = realName.getText().toString().trim();
                String enteredPhone = phone.getText().toString().trim();
                String enteredEmail = email.getText().toString().trim();
                String enteredHome = home.getText().toString().trim();
                String enteredBirthday = birthday.getText().toString().trim();

                // 檢查用戶是否有未填寫的資料
                if (TextUtils.isEmpty(enteredUsername) || TextUtils.isEmpty(enteredPassword) ||
                        TextUtils.isEmpty(enteredRealName) || TextUtils.isEmpty(enteredPhone) ||
                        TextUtils.isEmpty(enteredEmail) || TextUtils.isEmpty(enteredHome) ||
                        TextUtils.isEmpty(enteredBirthday) || TextUtils.isEmpty(enteredPasswordCheck)) {

                    Toast.makeText(requireContext(), "有資料未填寫，無法註冊", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 檢查密碼和確認密碼是否相同
                if (!enteredPassword.equals(enteredPasswordCheck)) {
                    Toast.makeText(requireContext(), "確認密碼與密碼不相符", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 檢查用戶名是否已存在
                if (UserManager.getInstance().getUser(enteredUsername) != null) {
                    Toast.makeText(requireContext(), "用戶名已存在，請輸入其他用戶名", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 檢查電話號碼格式是否正確
                if (!enteredPhone.matches("\\d{4}-\\d{3}-\\d{3}")) {
                    Toast.makeText(requireContext(), "電話號碼格式不正確，應為0000-000-000", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 使用正則表達式檢查郵件地址的格式是否正確
                if (!isValidEmail(enteredEmail)) {
                    Toast.makeText(requireContext(), "郵件地址格式不正確", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 檢查住址是否符合一般格式
                if (!isValidAddress(enteredHome)) {
                    Toast.makeText(requireContext(), "請輸入有效的住址(00縣市00鄉鎮市區00街弄路00號)", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 將密碼進行雜湊
                String hashedPassword = hashPassword(enteredPassword);

                // 發送註冊請求到API
                new RegisterUserTask().execute(enteredUsername, hashedPassword, enteredEmail, enteredRealName, enteredPhone, enteredHome, enteredBirthday);
            }
        });

        return root;
    }

    // 檢查郵件地址格式是否正確
    private boolean isValidEmail(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        return email.matches(emailPattern);
    }

    // 檢查住址格式是否正確
    private boolean isValidAddress(String address) {
        String addressPattern = ".*\\d+號.*";
        return address.matches(addressPattern);
    }

    // 將密碼進行雜湊的方法
    private String hashPassword(String password) {
        try {
            // 使用SHA-256算法進行雜湊
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(password.getBytes());
            byte[] hashedPasswordBytes = md.digest();

            // 將byte數組轉換為十六進制字符串
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedPasswordBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}
