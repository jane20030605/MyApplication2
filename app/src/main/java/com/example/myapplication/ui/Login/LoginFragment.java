package com.example.myapplication.ui.Login;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.models.User;
import com.example.myapplication.utils.SessionManager;
import com.example.myapplication.utils.UserManager;

public class LoginFragment extends Fragment {

    private SharedPreferences sharedPreferences; // 偏好設置
    private SessionManager sessionManager; // 會話管理器

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        LoginViewModel loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        View root = inflater.inflate(R.layout.fragment_login, container, false);

        sharedPreferences = requireContext().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        sessionManager = new SessionManager(requireContext()); // 初始化會話管理器

        final EditText editText = root.findViewById(R.id.editText);
        final EditText passwordEditText = root.findViewById(R.id.password);
        final Button loginButton = root.findViewById(R.id.button11);
        final Button registerButton = root.findViewById(R.id.button12);
        final Button forgotPasswordButton = root.findViewById(R.id.forgot_password);
        final CheckBox rememberPasswordCheckBox = root.findViewById(R.id.remember_password);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = editText.getText().toString();
                String password = passwordEditText.getText().toString();

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(requireContext(), "請輸入使用者名稱和密碼", Toast.LENGTH_SHORT).show();
                } else {
                    User user = UserManager.getInstance().getUser(username);
                    if (user != null && user.getPassword().equals(password)) {
                        Toast.makeText(requireContext(), "登入成功", Toast.LENGTH_SHORT).show();
                        Navigation.findNavController(v).navigate(R.id.nav_home); // 導航到首頁

                        // 更新菜單項目
                        updateMenuItems();

                        // 保存使用者名稱到 SharedPreferences
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("USERNAME", username);
                        editor.apply();

                        // 傳遞使用者名稱並導航到 MainActivity
                        Intent intent = new Intent(requireActivity(), MainActivity.class);
                        intent.putExtra("USERNAME", username);
                        startActivity(intent);

                        // 如果勾選了"記住密碼"，則保存密碼狀態
                        if (rememberPasswordCheckBox.isChecked()) {
                            sessionManager.login();//登入狀態
                            editor = sharedPreferences.edit();
                            editor.putBoolean("記住密碼", true);
                            editor.putString("密碼:", password);
                            editor.apply();
                        }else{
                            sessionManager.login(); // 仍然為登入狀態
                        }
                    } else {
                        Toast.makeText(requireContext(), "使用者名稱或密碼錯誤", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.nav_registration); // 導航到註冊頁面
            }
        });

        forgotPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.nav_forget_password); // 導航到忘記密碼頁面
            }
        });

        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String username = editText.getText().toString();
                    boolean rememberPassword = sharedPreferences.getBoolean("記住密碼", false);
                    if (rememberPassword && UserManager.getInstance().getUser(username) != null) {
                        showFillPasswordDialog(passwordEditText);
                    }
                }
            }
        });

        return root;
    }

    // 顯示填充密碼的對話框
    private void showFillPasswordDialog(EditText passwordEditText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("填充密碼");
        builder.setMessage("是否填充保存的密碼？");
        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String savedPassword = sharedPreferences.getString("密碼:", "");
                passwordEditText.setText(savedPassword);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    // 更新菜單項目
    private void updateMenuItems() {
        if (getActivity() != null && getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).updateMenuItems();
        }
    }
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // 更新菜單項目
        updateMenuItems();
    }

}
