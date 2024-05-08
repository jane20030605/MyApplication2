package com.example.myapplication.ui.forget_password;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.myapplication.databinding.FragmentForgetPasswordBinding;

public class ForgetPasswordFragment extends Fragment {

    private FragmentForgetPasswordBinding binding;
    private ForgetPasswordViewModel forgetPasswordViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        forgetPasswordViewModel = new ForgetPasswordViewModel();

        // 使用 Data Binding 綁定 Fragment 的布局
        binding = FragmentForgetPasswordBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // 獲取 EditText、Button 和 TextView 控件
        final EditText editEmail = binding.editEmail;
        final Button buttonSubmit = binding.buttonSubmit;
        final TextView textInstructions = binding.textInstructions;

        // 設置按鈕的點擊監聽器
        buttonSubmit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // 獲取用戶輸入的電子郵件地址
                String email = editEmail.getText().toString().trim();

                if (!email.isEmpty()) {
                    // 調用 ViewModel 中的方法發送新密碼
                    boolean sent = forgetPasswordViewModel.sendNewPassword(email);
                    if (sent) {
                        // 提示用戶新密碼已發送
                        textInstructions.setText("新密碼已發送");
                        // 發送成功後導航回上一個目的地
                        Navigation.findNavController(v).navigateUp();
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
