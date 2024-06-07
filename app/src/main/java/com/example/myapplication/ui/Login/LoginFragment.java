package com.example.myapplication.ui.Login;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.utils.SessionManager;
import com.example.myapplication.utils.UserManager;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginFragment extends Fragment {

    private SharedPreferences sharedPreferences; // 偏好設置
    private SessionManager sessionManager; // 會話管理器
    private Handler mainHandler = new Handler(Looper.getMainLooper()); // 主線程的 Handler

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_login, container, false);

        // 初始化偏好設置和會話管理器
        sharedPreferences = requireContext().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        sessionManager = new SessionManager(requireContext());

        // 獲取 layout 中的元件
        final EditText editText = root.findViewById(R.id.editText);
        final EditText passwordEditText = root.findViewById(R.id.password);
        final Button loginButton = root.findViewById(R.id.button11);
        final Button registerButton = root.findViewById(R.id.button12);
        final Button forgotPasswordButton = root.findViewById(R.id.forgot_password);
        final CheckBox rememberPasswordCheckBox = root.findViewById(R.id.remember_password);

        // 登入按鈕的點擊事件
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = editText.getText().toString();
                String password = passwordEditText.getText().toString();

                // 檢查使用者名稱和密碼是否為空
                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(requireContext(), "請輸入使用者名稱及密碼", Toast.LENGTH_SHORT).show();
                } else {
                    // 呼叫登入 API 客戶端進行登入
                    login(username, password);
                }
            }
        });

        // 導航到註冊介面
        registerButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseRequireInsteadOfGet")
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.nav_registration);
            }
        });

        // 導航到忘記密碼介面
        forgotPasswordButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseRequireInsteadOfGet")
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.nav_forget_password);
            }
        });

        // 當使用者名稱的 EditText 失去焦點時，檢查是否需要填充保存的密碼
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
                String savedPassword = sharedPreferences.getString("密碼", "");
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

    // 登入方法
    private void login(String username, String hashpassword) {
        // 建立 OkHttpClient
        OkHttpClient client = new OkHttpClient();

        // 建立 FormBody，將使用者名稱和密碼添加到請求中
        RequestBody formBody = new FormBody.Builder()
                .add("account", username)
                .add("password", hashpassword)
                .build();

        // 建立 POST 請求
        Request request = new Request.Builder()
                .url("http://100.96.1.3/api_login.php")
                .post(formBody)
                .build();

        // 執行請求
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(requireContext(), "登入失敗，請檢查網路連接", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseData = response.body().string();
                if (responseData.contains("登入成功")) {
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            // 登入成功，執行相應操作
                            handleLoginSuccess(username, hashpassword);
                        }
                    });
                } else {
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            // 登入失敗，顯示錯誤訊息
                            Toast.makeText(requireContext(), "登入失敗，請檢查帳號和密碼", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    // 登入成功處理
    private void handleLoginSuccess(String username, String password) {
        // 保存使用者名稱到偏好設置
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("USERNAME", username);
        editor.apply();

        // 導航到 MainActivity
        Intent intent = new Intent(requireActivity(), MainActivity.class);
        intent.putExtra("USERNAME", username);
        startActivity(intent);
        Toast.makeText(requireContext(), "登入成功", Toast.LENGTH_SHORT).show();
        // 如果需要記住密碼，保存使用者名稱和密碼到偏好設置
        CheckBox rememberPasswordCheckBox = requireView().findViewById(R.id.remember_password);
        if (rememberPasswordCheckBox.isChecked()) {
            sessionManager.login();
            editor = sharedPreferences.edit();
            editor.putString("使用者名稱", username);
            editor.putString("密碼", password);
            editor.putBoolean("記住密碼", true);
            editor.apply();
        } else {
            sessionManager.login();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // 更新選單項目
        updateMenuItems();
    }

    // 更新選單項目
    private void updateMenuItems() {
        if (getActivity() != null && getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).updateMenuItems();
        }
    }
}
