package com.example.myapplication.ui.forget_password;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentForgetPasswordBinding;
import com.example.myapplication.databinding.FragmentLoginBinding;
import com.example.myapplication.ui.Login.LoginViewModel;
import com.example.myapplication.utils.SessionManager;

public class forget_password extends Fragment {

    private FragmentForgetPasswordBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ForgetPasswordViewModel forgetPasswordViewModel = new ViewModelProvider(this).get(ForgetPasswordViewModel.class);

        binding = FragmentForgetPasswordBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final EditText editEmail = binding.editEmail;
        final Button buttonSubmit = binding.buttonSubmit ;

        // 在這裡設置按鈕的點擊監聽器，你可以在點擊時執行所需的操作
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 在這裡處理提交按鈕的點擊事件
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