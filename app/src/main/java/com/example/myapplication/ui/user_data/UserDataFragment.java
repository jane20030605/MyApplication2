package com.example.myapplication.ui.user_data;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentUserDataBinding;
import com.example.myapplication.utils.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UserDataFragment extends Fragment {

    private FragmentUserDataBinding binding;  // 用於綁定視圖的變量
    private UserUpdateClient apiClient;  // 用於處理API請求的客戶端
    private UserUpdateEvent userUpdateEvent;  // 儲存用戶更新事件數據的變量

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiClient = new UserUpdateClient();  // 初始化API客戶端
        Log.d("UserDataFragment", "onCreate: 初始化 apiClient");

        // 創建ViewModel
        UserDataViewModel userDataViewModel = new ViewModelProvider(this).get(UserDataViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentUserDataBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize SessionManager
        SessionManager sessionManager = new SessionManager(requireContext());
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        String account = sharedPreferences.getString("ACCOUNT", "");

        // Check if logged in, navigate to login page if not
        if (account == null || account.isEmpty()) {
            if (getActivity() != null) {
                Navigation.findNavController(getActivity(), R.id.nav_user_data).navigate(R.id.nav_login);
            }
            return root;
        }

        Log.d("UserDataFragment", "Logged in account: " + account);

        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\"account\": \"" + account + "\"}");
        Request request = new Request.Builder()
                .url("http://100.96.1.3/api_update_userdata.php")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), "Network request failed", Toast.LENGTH_SHORT).show();
                });
                Log.e("UserDataFragment", "Network request failed", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }
                String responseBody = response.body().string();
                Log.e("Body:", responseBody);

                requireActivity().runOnUiThread(() -> {
                    try {
                        JSONObject jsonObject = new JSONObject(responseBody);
                        JSONObject data = jsonObject.getJSONObject("data");
                        userUpdateEvent = new UserUpdateEvent();
                        userUpdateEvent.setAccount(account);
                        userUpdateEvent.setAddress(data.getString("address"));
                        userUpdateEvent.setBirthday(data.getString("birthday"));
                        userUpdateEvent.setMail(data.getString("mail"));
                        userUpdateEvent.setTel(data.getString("tel"));
                        userUpdateEvent.setName(data.getString("name"));

                        updateNavHeaderMail(requireActivity(), userUpdateEvent.getMail());

                        binding.editTextAddress.setText(userUpdateEvent.getAddress());
                        binding.editTextBirthday.setText(userUpdateEvent.getBirthday());
                        binding.editTextMail.setText(userUpdateEvent.getMail());
                        binding.editTextName.setText(userUpdateEvent.getName());
                        binding.editTextTel.setText(userUpdateEvent.getTel());
                        Log.d("UserDataFragment", "Data loaded from args and set into views");
                    } catch (JSONException e) {
                        Log.e("JSON Parsing Error:", e.getMessage());
                        Toast.makeText(requireContext(), "Failed to parse data", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            private void updateNavHeaderMail(Activity activity, String email) {
                if (activity instanceof MainActivity) {
                    ((MainActivity) activity).updateNavHeaderEmail(email);
                }
            }
        });

        setupUIListeners();
        return root;
    }


    private void setupUIListeners() {
        // 點擊保存按鈕的事件處理
        binding.buttonDataSave.setOnClickListener(v -> {
            try {
                saveEvent();  // 保存數據
                Navigation.findNavController(v).navigate(R.id.nav_home);  // 導航到日曆頁面
                Toast.makeText(requireContext(), "更新成功，已上傳資料\n請下拉頁面刷新", Toast.LENGTH_SHORT).show();
                Log.d("UserDataFragment", "buttonDataSave: 點擊保存按鈕，數據更新成功");
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });

        // 點擊新增緊急聯絡人的事件處理
        binding.buttonEmergencyContact.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "新增緊急聯絡人", Toast.LENGTH_SHORT).show();
            Navigation.findNavController(v).navigate(R.id.nav_emergency_contact);  // 導航到新增緊急聯絡人頁面
            Log.d("UserDataFragment", "buttonEmergencyContact: 點擊新增緊急聯絡人按鈕");
        });

        // 點擊更改密碼按鈕的事件處理
        binding.buttonCancel.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "進行密碼更改", Toast.LENGTH_SHORT).show();
            Navigation.findNavController(v).navigate(R.id.nav_password_change);  // 導航到更改密碼頁面
            Log.d("UserDataFragment", "buttonCancel: 點擊更改密碼按鈕");
        });

        // 點擊緊急聯絡人列表按鈕的事件處理
        binding.buttonContact.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "緊急聯絡人列表", Toast.LENGTH_SHORT).show();
            Navigation.findNavController(v).navigate(R.id.nav_contact_list);  // 導航到緊急聯絡人列表頁面
            Log.d("UserDataFragment", "buttonContact: 點擊緊急聯絡人列表按鈕");
        });

        // 使用者名稱輸入完成後焦點移動到郵件輸入框
        binding.editTextName.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                binding.editTextMail.requestFocus();
                Log.d("UserDataFragment", "editTextUsername: 使用者名稱輸入完成，焦點移動到郵件輸入框");
                return true;
            }
            return false;
        });

        // 點擊生日輸入框顯示日期選擇對話框
        binding.editTextBirthday.setOnClickListener(v -> showDatePickerDialog(binding.editTextBirthday));
    }

    private void saveEvent() throws JSONException {
        if (userUpdateEvent == null) {
            return;
        }

        // 從視圖中獲取用戶輸入的數據
        String username = binding.editTextName.getText().toString();
        String email = binding.editTextMail.getText().toString();
        String phone = binding.editTextTel.getText().toString();
        String address = binding.editTextAddress.getText().toString();
        String birthday = binding.editTextBirthday.getText().toString();

        // 添加日誌來確認從UI中獲取的數據是否正確
        Log.d("UserDataFragment", "saveEvent: " +
                "username = " + username + ", email = " + email + ", phone = " + phone + ", " +
                "address = " + address + ", birthday = " + birthday);

        // 獲取存儲的帳戶信息
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        String account = sharedPreferences.getString("ACCOUNT", "");

        // 檢查是否有任何字段是空的
        if (username.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty() || birthday.isEmpty()) {
            Toast.makeText(requireContext(), "請填寫所有字段", Toast.LENGTH_SHORT).show();
            return;
        }

        // 更新 userUpdateEvent 的數據
        userUpdateEvent.setName(username);
        userUpdateEvent.setMail(email);
        userUpdateEvent.setTel(phone);
        userUpdateEvent.setAddress(address);
        userUpdateEvent.setBirthday(birthday);

        // 記錄所有更新的數據
        Log.d("UserDataFragment", "保存的所有更新數據: " + userUpdateEvent.getAllValue());

        // 將更新的數據上傳到伺服器
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, new JSONObject()
                .put("account", account)
                .put("name", username)
                .put("mail", email)
                .put("tel", phone)
                .put("address", address)
                .put("birthday", birthday)
                .toString());
        Request request = new Request.Builder()
                .url("http://100.96.1.3/api_update_userdata.php")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), "網路請求失敗", Toast.LENGTH_SHORT).show();
                });
                Log.e("UserDataFragment", "網路請求失敗", e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(requireContext(), "更新失敗", Toast.LENGTH_SHORT).show();
                    });
                    throw new IOException("Unexpected code " + response);
                }
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), "資料更新成功", Toast.LENGTH_SHORT).show();
                });
                Log.d("UserDataFragment", "資料更新成功");
            }
        });
    }

    private void showDatePickerDialog(TextView textView) {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, monthOfYear, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            String format = "yyyy-MM-dd";  // 設置日期格式
            SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.TAIWAN);  // 設置日期格式
            textView.setText(sdf.format(calendar.getTime()));  // 顯示選擇的日期
        };

        new DatePickerDialog(requireContext(), dateSetListener,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
