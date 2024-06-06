package com.example.myapplication.ui.change_password;

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
import android.widget.Toast;

import com.example.myapplication.R;

public class password_changeFragment extends Fragment {

    private PasswordChangeViewModel mViewModel;

    private EditText etCurrentPassword;
    private EditText etNewPassword;
    private EditText etConfirmPassword;
    private Button btnConfirmChange;
    private Button btnSubmit;

    public static password_changeFragment newInstance() {
        return new password_changeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_password_change, container, false);

        // 初始化View
        etCurrentPassword = rootView.findViewById(R.id.et_current_password);
        etNewPassword = rootView.findViewById(R.id.et_new_password);
        etConfirmPassword = rootView.findViewById(R.id.et_confirm_password);
        btnConfirmChange = rootView.findViewById(R.id.btn_confirm_change);
        btnSubmit = rootView.findViewById(R.id.btn_submit);

        // 設置按鈕點擊事件
        btnConfirmChange.setOnClickListener(v -> confirmChange());
        btnSubmit.setOnClickListener(v -> submit());

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(PasswordChangeViewModel.class);
        // TODO: Use the ViewModel
    }

    private void confirmChange() {
        // 獲取輸入的密碼
        String currentPassword = etCurrentPassword.getText().toString();
        String newPassword = etNewPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();

        // 簡單驗證密碼是否一致
        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(getActivity(), "新密碼和確認密碼不一致", Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO: 這裡添加更多的邏輯來處理密碼變更
        Toast.makeText(getActivity(), "確認修改按鈕被點擊", Toast.LENGTH_SHORT).show();
    }

    private void submit() {
        // TODO: 提交邏輯
        Toast.makeText(getActivity(), "提交按鈕被點擊", Toast.LENGTH_SHORT).show();
    }
}
