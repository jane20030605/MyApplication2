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
import com.example.myapplication.models.User;
import com.example.myapplication.utils.UserManager;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;

public class RegistrationFragment extends Fragment {

    private FragmentRegistrationBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // 創建ViewModelProvider以便獲取RegistrationViewModel實例
        RegistrationViewModel registrationViewModel =
                new ViewModelProvider(this).get(RegistrationViewModel.class);

        // 載入布局檔並綁定
        binding = FragmentRegistrationBinding.inflate(inflater, container, false);
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
                // 獲取當前日期
                final Calendar calendar = Calendar.getInstance();
                int mYear = calendar.get(Calendar.YEAR);
                int mMonth = calendar.get(Calendar.MONTH);
                int mDay = calendar.get(Calendar.DAY_OF_MONTH);

                // 創建DatePickerDialog並顯示
                DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // 將年、月、日組合成日期字串，格式為YYYY/MM/DD
                                @SuppressLint("DefaultLocale")
                                String formattedDate = String.format
                                        ("%04d/%02d/%02d", year, (monthOfYear + 1), dayOfMonth);
                                // 設定生日欄位的文本
                                birthday.setText(formattedDate);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        // 設定電話號碼格式
        phone.addTextChangedListener(new TextWatcher() {
            // 標誌是否正在格式化電話號碼
            private boolean isFormatting;
            // 輸入變化後的字符數
            private int after;
            // 電話號碼的總長度，包括分隔符
            private static final int TOTAL_SYMBOLS = 12; // 格式為0000-000-000
            // 電話號碼的總數字數量
            private static final int TOTAL_DIGITS = 10; // 數字總數為10
            // 第一個分隔符位置
            private static final int FIRST_DIVIDER_POSITION = 5; // 第一個分隔符位置為5
            // 第二個分隔符位置
            private static final int SECOND_DIVIDER_POSITION = 9; // 第二個分隔符位置為9
            // 分隔符
            private static final char DIVIDER = '-';

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // 獲取輸入變化後的字符數
                this.after = after;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 當有輸入變化且不在格式化過程中時
                if (s.length() > 0 && !isFormatting) {
                    isFormatting = true;
                    // 如果是添加字符且在需要插入分隔符的位置
                    if (after > 0 && (s.length() == FIRST_DIVIDER_POSITION || s.length() == SECOND_DIVIDER_POSITION)) {
                        // 當前長度小於總符號長度時插入分隔符
                        if (s.length() < TOTAL_SYMBOLS) {
                            phone.setText(new StringBuilder(s).insert(s.length() - 1, DIVIDER).toString());
                            phone.setSelection(phone.getText().length());
                        }
                    } else if (after == 0 && s.length() > 0 && (s.length() == FIRST_DIVIDER_POSITION || s.length() == SECOND_DIVIDER_POSITION)) {
                        // 如果是刪除字符且在分隔符的位置
                        phone.setText(s.subSequence(0, s.length() - 1));
                        phone.setSelection(phone.getText().length());
                    }
                    isFormatting = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // 如果超過10個字符，則刪除多餘字符
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

    // 註冊用戶的異步任務
    private class RegisterUserTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            // 從參數中獲取註冊資料
            String enteredUsername = params[0];
            String enteredPassword = params[1];
            String enteredEmail = params[2];
            String enteredRealName = params[3];
            String enteredPhone = params[4];
            String enteredHome = params[5];
            String enteredBirthday = params[6];

            try {
                // 設定API網址
                URL url = new URL("http://100.96.1.3/api_register.php");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);

                // 構建POST請求參數
                String postData = "username=" + enteredUsername + "&password=" + enteredPassword +
                        "&email=" + enteredEmail + "&real_name=" + enteredRealName +
                        "&phone=" + enteredPhone + "&home=" + enteredHome + "&birthday=" + enteredBirthday;

                // 發送請求參數
                OutputStream os = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(postData);
                writer.flush();
                writer.close();
                os.close();

                // 檢查響應碼
                int responseCode = urlConnection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    return "註冊成功";
                } else {
                    return "註冊失敗，請稍後再試";
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "註冊失敗，請稍後再試";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            // 顯示註冊結果
            Toast.makeText(getContext(), result, Toast.LENGTH_LONG).show();
            if (result.equals("註冊成功")) {
                // 導航到下一個Fragment或Activity
                Navigation.findNavController(getView()).navigate(R.id.nav_login);
            }
        }
    }
}
