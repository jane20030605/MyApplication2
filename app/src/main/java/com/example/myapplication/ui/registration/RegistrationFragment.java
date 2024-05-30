package com.example.myapplication.ui.registration;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
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
                // 創建新用戶並添加到管理器中
                User newUser = new User(enteredUsername, enteredPassword);
                UserManager.getInstance().addUser(newUser);

                // 保存資訊到 SharedPreferences
                saveUserInfoToSharedPreferences(enteredRealName, enteredEmail, enteredPhone, enteredHome, enteredBirthday);

                // 提示用戶註冊成功
                Toast.makeText(requireContext(), "註冊成功，請重新登入", Toast.LENGTH_SHORT).show();

                // 導航到登入頁面並传递电子邮箱参数
                Bundle bundle = new Bundle();
                bundle.putString("EMAIL", enteredEmail);
                Navigation.findNavController(v).navigate(R.id.nav_login, bundle);
            }
        });

        return root;
    }
    // 檢查住址是否符合一般格式
    private boolean isValidAddress(String address) {
        // 這裡可以根據你的實際需求定義住址的驗證邏輯
        // 例如: 某某市某某區某某街某某號
        return address.matches(".*[市縣].*[鄉鎮市區].*[街路弄].*號.*");
    }


    // 檢查郵件地址格式是否正確的方法
    private boolean isValidEmail(String email) {
        // 使用正則表達式來檢查郵件地址格式
        String emailPattern = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}";
        return email.matches(emailPattern);
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // 釋放綁定
        binding = null;
    }

    // 將用戶信息保存到SharedPreferences
    private void saveUserInfoToSharedPreferences(String real_name, String email, String phone, String address, String birthday) {
        // 獲取SharedPreferences實例
        SharedPreferences sharedPreferences =
                requireContext().getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        // 保存用戶信息
        editor.putString("REAL NAME", real_name);
        editor.putString("EMAIL", email);
        editor.putString("PHONE", phone);
        editor.putString("ADDRESS", address);
        editor.putString("BIRTHDAY", birthday);
        // 提交修改
        editor.apply();
    }

}
