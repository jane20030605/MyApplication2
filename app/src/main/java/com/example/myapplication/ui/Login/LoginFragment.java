package com.example.myapplication.ui.Login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.databinding.FragmentLoginBinding;

public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        LoginViewModel loginViewModel =
                new ViewModelProvider(this).get(LoginViewModel.class);

        binding = FragmentLoginBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final EditText editText = binding.editText;
        final EditText passwordEditText = binding.password;
        final CheckBox rememberPasswordCheckBox = binding.rememberPassword;
        final Button loginButton = binding.button11;
        final Button registerButton = binding.button12;
        final Button googleButton = binding.button14;


        // 登錄按鈕的點擊事件
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = editText.getText().toString();
                String password = passwordEditText.getText().toString();
                // 在這裡添加登錄邏輯
                // 範例中只是顯示一個Toast消息
                Toast.makeText(requireContext(), "用戶名稱：" + username + "，密碼：" + password, Toast.LENGTH_SHORT).show();
            }
        });

        // 註冊按鈕的點擊事件
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 在這裡添加註冊邏輯
                // 範例中只是顯示一個Toast消息
                Toast.makeText(requireContext(), "註冊功能尚未實現", Toast.LENGTH_SHORT).show();
            }
        });

        // Google按鈕的點擊事件
        googleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 在這裡添加Google登錄邏輯
                // 範例中只是顯示一個Toast消息
                Toast.makeText(requireContext(), "Google 登錄功能尚未實現", Toast.LENGTH_SHORT).show();
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
