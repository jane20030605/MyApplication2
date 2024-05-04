package com.example.myapplication.ui.registration;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
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
        RegistrationViewModel registrationViewModel =
                new ViewModelProvider(this).get(RegistrationViewModel.class);

        // 載入布局檔
        binding = FragmentRegistrationBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // 獲取輸入欄位及按鈕
        final EditText username = binding.username;
        final EditText password = binding.password;
        final EditText passwordCheck = binding.passwordCheck; // 新增確認密碼輸入欄位
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
                                // 將年、月、日組合成日期字串，這裡假設您希望日期格式為YYYY/MM/DD
                                @SuppressLint("DefaultLocale")
                                String formattedDate = String.format
                                        ("%04d/%02d/%02d", year, (monthOfYear + 1), dayOfMonth);
                                birthday.setText(formattedDate);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        // 設定註冊按鈕點擊事件
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 獲取用戶輸入的資料
                String enteredUsername = username.getText().toString().trim();
                String enteredPassword = password.getText().toString().trim();
                String enteredPasswordCheck = passwordCheck.getText().toString().trim(); // 新增確認密碼輸入文本
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

                // 創建新用戶並添加到管理器中
                User newUser = new User(enteredUsername, enteredPassword);
                // 您可能希望在這裡設置newUser的其他欄位
                UserManager.getInstance().addUser(newUser);

                // 提示用戶註冊成功
                Toast.makeText(requireContext(), "註冊成功，請重新登入", Toast.LENGTH_SHORT).show();
                // 導航到登入頁面
                Navigation.findNavController(v).navigate(R.id.nav_login);
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
