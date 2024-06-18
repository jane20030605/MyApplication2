package com.example.myapplication.ui.user_data;

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
        // 綁定視圖
        binding = FragmentUserDataBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // 初始化Session管理器
        SessionManager sessionManager = new SessionManager(requireContext());
        Log.d("UserDataFragment", "onCreateView: 初始化 sessionManager");
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        String account = sharedPreferences.getString("ACCOUNT", "");
        Log.d("UserDataFragment", "登入帳戶為:" + account);
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\"account\": \"" +  account +   "\"}");
        Request request = new Request.Builder()
                .url("http://100.96.1.3/api_update_userdata.php")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
               System.out.println("account is null ");
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
               if (!response.isSuccessful()){
                   throw new IOException("Unexpected code "  + response);
               }
               String responseBody = response.body().string();
               Log.e("Body:" , responseBody);

               try {
                   JSONObject jsonObject = new JSONObject(responseBody);
                   JSONObject data = jsonObject.getJSONObject("data");
                   String name = data.getString("name");
                   userUpdateEvent = new UserUpdateEvent();
                   userUpdateEvent.setAccount(account);
                   userUpdateEvent.setAddress(data.getString("address"));
                   userUpdateEvent.setBirthday(data.getString("birthday"));
                   userUpdateEvent.setMail(data.getString("mail"));
                   userUpdateEvent.setTel(data.getString("tel"));
                   userUpdateEvent.setName(data.getString("name"));
                   // 記錄從args中獲取的數據
                   Log.e("UserDataFragment", userUpdateEvent.getAllValue());
                   // 將數據設置到視圖中
                   binding.editTextAddress.setText(userUpdateEvent.getAddress());
                   binding.editTextBirthday.setText(userUpdateEvent.getBirthday());
                   binding.editTextMail.setText(userUpdateEvent.getMail());
                   binding.editTextName.setText(userUpdateEvent.getName());
                   binding.editTextTel.setText(userUpdateEvent.getTel());
                   Log.d("UserDataFragment", "從 args 中獲取數據並設置到視圖中");
               } catch (JSONException e) {
                   Log.e("JSON Parsing Error: " , e.getMessage());
                   throw new RuntimeException(e);
               }
            }
        });
        // 設置UI監聽器
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
            Toast.makeText(requireContext(), "請填寫所有數據", Toast.LENGTH_SHORT).show();
            Log.d("UserDataFragment", "saveEvent: 數據不完整");
            return;
        }

        // 檢查郵件地址格式是否正確
        if (!isValidEmail(email)) {
            Toast.makeText(requireContext(), "郵件地址格式不正確", Toast.LENGTH_SHORT).show();
            Log.d("UserDataFragment", "saveEvent: 郵件地址格式不正確");
            return;
        }

        // 設置UserUpdateEvent對象的數據
        userUpdateEvent.setName(username);
        userUpdateEvent.setTel(phone);
        userUpdateEvent.setMail(email);
        userUpdateEvent.setAddress(address);
        userUpdateEvent.setBirthday(birthday);
        userUpdateEvent.setAccount(account);

        // 創建要發送到API的JSON數據
        String saveUserData = createEventDataJson(userUpdateEvent);
        Log.d("UserDataFragment", "saveEvent: eventDataJson = " + saveUserData);

        // 調用API客戶端進行用戶數據更新
        apiClient.updateUserData(saveUserData, new UserUpdateClient.UserDataUpdateCallback() {
            @Override
            public void onSuccess(String message) {
                Log.d("UserDataFragment", "onSuccess: " + message);
            }

            @Override
            public void onError(String message) {
                Log.e("UserDataFragment", "onError: " + message);
            }
        });
    }

    private String createEventDataJson(UserUpdateEvent event) throws JSONException {
        // 創建一個JSON對象，並將用戶數據放入其中
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", event.getName());
        jsonObject.put("tel", event.getTel());
        jsonObject.put("mail", event.getMail());
        jsonObject.put("address", event.getAddress());
        jsonObject.put("birthday", event.getBirthday());
        jsonObject.put("account", event.getAccount());
        return jsonObject.toString();  // 返回JSON字符串
    }

    private void showDatePickerDialog(TextView editText) {
        // 獲取當前日期
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // 創建並顯示日期選擇對話框
        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                (view, year1, monthOfYear, dayOfMonth) -> {
                    // 設置選中的日期到日曆中
                    calendar.set(year1, monthOfYear, dayOfMonth);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    editText.setText(sdf.format(calendar.getTime()));  // 將日期設置到TextView中
                }, year, month, day);
        datePickerDialog.show();  // 顯示對話框
    }

    private boolean isValidEmail(CharSequence target) {
        // 檢查郵件地址格式是否正確
        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;  // 清理綁定
    }
}
