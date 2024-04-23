package com.example.myapplication.ui.forget_password;

import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentForgetPasswordBinding;

public class forget_password extends Fragment {

    private FragmentForgetPasswordBinding binding;
    private ForgetPasswordViewModel forgetPasswordViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // 初始化 ViewModel
        forgetPasswordViewModel = new ViewModelProvider(this).get(ForgetPasswordViewModel.class);

        // 使用 Data Binding 綁定布局
        binding = FragmentForgetPasswordBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // 獲取布局中的 EditText、Button 和 TextView 控件
        final EditText editEmail = binding.editEmail;
        final Button buttonSubmit = binding.buttonSubmit;
        final TextView textInstructions = binding.textInstructions;

        // 設置按鈕的點擊監聽器
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                // 獲取用戶輸入的電子郵件地址
                String email = editEmail.getText().toString().trim();
                // 檢查是否輸入了電子郵件地址
                if (!email.isEmpty()) {
                    // 調用 ViewModel 中的方法發送新密碼
                    boolean sent = forgetPasswordViewModel.sendNewPassword(email);
                    if (sent) {
                        // 提示用戶新密碼已發送
                        textInstructions.setText("新密碼已發送");
                    } else {
                        // 如果發送失敗，顯示錯誤提示
                        textInstructions.setText("發送失敗，請檢查電子郵件地址");
                    }
                } else {
                    // 如果用戶未輸入電子郵件地址，顯示錯誤提示
                    textInstructions.setText("請輸入電子郵件地址");
                }
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
