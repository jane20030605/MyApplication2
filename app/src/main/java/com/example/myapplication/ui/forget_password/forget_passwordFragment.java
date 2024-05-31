package com.example.myapplication.ui.forget_password;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentForgetPasswordBinding;
import com.example.myapplication.Request.ResetPasswordRequest;
import com.example.myapplication.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class forget_passwordFragment extends Fragment {

    // 定義 View Binding 變數
    private FragmentForgetPasswordBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 使用 View Binding 來膨脹佈局
        binding = FragmentForgetPasswordBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // 使用 View Binding 設置按鈕點擊監聽器
        binding.buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitEmail();
            }
        });

        return view;
    }

    // 提交郵件的方法
    private void submitEmail() {
        String email = binding.editEmail.getText().toString().trim();

        // 檢查郵件地址是否為空
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getActivity(), R.string.enter_email, Toast.LENGTH_SHORT).show();
            return;
        }

        // 創建 Retrofit 實例和 ApiService
        forget_passwordApiService apiService = RetrofitClient.getClient
                ().create(forget_passwordApiService.class);

        // 創建重置密碼請求
        ResetPasswordRequest request = new ResetPasswordRequest(email);

        // 發送重置密碼請求
        apiService.resetPassword(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getActivity(), "重置密碼郵件已發送到 " + email, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "發送失敗，請稍後重試", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Toast.makeText(getActivity(), "網絡錯誤，請稍後重試", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // 將 binding 設置為 null，以避免記憶體洩漏
        binding = null;
    }
}
