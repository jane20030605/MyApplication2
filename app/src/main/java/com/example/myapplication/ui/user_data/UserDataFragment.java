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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class UserDataFragment extends Fragment {

    private FragmentUserDataBinding binding;
    private UserUpdateClient apiClient;
    private SessionManager sessionManager;
    private UserUpdateEvent userUpdateEvent; // 新增這一行
    private UserDataViewModel userDataViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiClient = new UserUpdateClient();
        Log.d("UserDataFragment", "onCreate: 初始化 apiClient");

        userDataViewModel = new ViewModelProvider(this).get(UserDataViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentUserDataBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        sessionManager = new SessionManager(requireContext());
        Log.d("UserDataFragment", "onCreateView: 初始化 sessionManager");

        Bundle args = getArguments();
        if (args != null && args.containsKey("account")) {
            userUpdateEvent = new UserUpdateEvent(); // 初始化 UserUpdateEvent
            userUpdateEvent.setAccount(args.getString("account"));
            userUpdateEvent.setAddress(args.getString("address"));
            userUpdateEvent.setBirthday(args.getString("birthday"));
            userUpdateEvent.setMail(args.getString("mail"));
            userUpdateEvent.setTel(args.getString("tel"));
            userUpdateEvent.setName(args.getString("name"));

            binding.editTextAddress.setText(userUpdateEvent.getAddress());
            binding.editTextBirthday.setText(userUpdateEvent.getBirthday());
            binding.editTextEmail.setText(userUpdateEvent.getMail());
            binding.editTextUsername.setText(userUpdateEvent.getName());
            binding.editTextPhone.setText(userUpdateEvent.getTel());
            Log.d("UserDataFragment", "從 args 中獲取數據並設置到視圖中");
        } else {
            Log.d("UserDataFragment", "未收到參數或缺少 'account' 鍵");
        }

        setupUIListeners();

        return root;
    }

    private void setupUIListeners() {
        binding.buttonDataSave.setOnClickListener(v -> {
            try {
                saveEvent();
                Navigation.findNavController(v).navigate(R.id.nav_calender);
                Toast.makeText(requireContext(), "更新成功，已上傳資料\n請下拉頁面刷新", Toast.LENGTH_SHORT).show();
                Log.d("UserDataFragment", "buttonDataSave: 點擊保存按鈕，數據更新成功");
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });

        binding.buttonEmergencyContact.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "新增緊急聯絡人", Toast.LENGTH_SHORT).show();
            Navigation.findNavController(v).navigate(R.id.nav_emergency_contact);
            Log.d("UserDataFragment", "buttonEmergencyContact: 點擊新增緊急聯絡人按鈕");
        });

        binding.buttonCancel.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "進行密碼更改", Toast.LENGTH_SHORT).show();
            Navigation.findNavController(v).navigate(R.id.nav_password_change);
            Log.d("UserDataFragment", "buttonCancel: 點擊更改密碼按鈕");
        });

        binding.buttonContact.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "緊急聯絡人列表", Toast.LENGTH_SHORT).show();
            Navigation.findNavController(v).navigate(R.id.nav_contact_list);
            Log.d("UserDataFragment", "buttonContact: 點擊緊急聯絡人列表按鈕");
        });

        binding.editTextUsername.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                binding.editTextEmail.requestFocus();
                Log.d("UserDataFragment", "editTextUsername: 使用者名稱輸入完成，焦點移動到郵件輸入框");
                return true;
            }
            return false;
        });

        binding.editTextBirthday.setOnClickListener(v -> showDatePickerDialog(binding.editTextBirthday));
    }

    private void saveEvent() throws JSONException {
        if (userUpdateEvent == null) {
            return;
        }

        String username = binding.editTextUsername.getText().toString();
        String email = binding.editTextEmail.getText().toString();
        String phone = binding.editTextPhone.getText().toString();
        String address = binding.editTextAddress.getText().toString();
        String birthday = binding.editTextBirthday.getText().toString();

        // 添加日誌來確認從 UI 中獲取的數據是否正確
        Log.d("UserDataFragment", "saveEvent: " +
                "username = " + username + ", email = " + email + ", phone = " + phone + ", " +
                "address = " + address + ", birthday = " + birthday);

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        String account = sharedPreferences.getString("ACCOUNT", "");

        if (username.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty() || birthday.isEmpty()) {
            Toast.makeText(requireContext(), "請填寫所有數據", Toast.LENGTH_SHORT).show();
            Log.d("UserDataFragment", "saveEvent: 數據不完整");
            return;
        }

        if (!isValidEmail(email)) {
            Toast.makeText(requireContext(), "郵件地址格式不正確", Toast.LENGTH_SHORT).show();
            Log.d("UserDataFragment", "saveEvent: 郵件地址格式不正確");
            return;
        }

        // 設置 UserUpdateEvent 對象的數據
        userUpdateEvent.setName(username);
        userUpdateEvent.setTel(phone);
        userUpdateEvent.setMail(email);
        userUpdateEvent.setAddress(address);
        userUpdateEvent.setBirthday(birthday);
        userUpdateEvent.setAccount(account);

        // 創建要發送到 API 的 JSON 數據
        String saveUserData = createEventDataJson(userUpdateEvent);
        Log.d("UserDataFragment", "saveEvent: eventDataJson = " + saveUserData);

        // 調用 API 客戶端進行用戶更新
        apiClient.updateUser(saveUserData, new UserUpdateClient.UserUpdateCallback() {
            @Override
            public void onSuccess(String message) {
                requireActivity().runOnUiThread(() -> {
                    handleApiResponse("{\"message\": \"" + message + "\"}");
                    Toast.makeText(requireContext(), "用戶數據更新成功：" + message, Toast.LENGTH_SHORT).show();
                    Log.d("UserDataFragment", "用戶數據更新成功：" + message);
                });
            }

            @Override
            public void onError(String message) {
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), "用戶數據更新失敗：" + message, Toast.LENGTH_SHORT).show();
                    Log.d("UserDataFragment", "用戶數據更新失敗：" + message);
                });
            }
        });
    }

    private String createEventDataJson(UserUpdateEvent userUpdateEvent) throws JSONException {
        JSONObject eventDataJson = new JSONObject();
        eventDataJson.put("tel", userUpdateEvent.getTel());
        eventDataJson.put("name", userUpdateEvent.getName());
        eventDataJson.put("address", userUpdateEvent.getAddress());
        eventDataJson.put("mail", userUpdateEvent.getMail());
        eventDataJson.put("birthday", userUpdateEvent.getBirthday());
        eventDataJson.put("account", userUpdateEvent.getAccount());
        Log.d("UserDataFragment", "createEventDataJson: 創建用戶數據的 JSON");
        return eventDataJson.toString();
    }

    private boolean isValidEmail(String email) {
        String emailPattern = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}";
        boolean isValid = email.matches(emailPattern);
        Log.d("UserDataFragment", "isValidEmail: 郵件地址 " + email + " 的有效性：" + isValid);
        return isValid;
    }

    private void showDatePickerDialog(final TextView editText) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                (view, year1, month1, dayOfMonth1) -> {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(year1, month1, dayOfMonth1);
                    editText.setText(dateFormat.format(selectedDate.getTime()));
                    Log.d("UserDataFragment", "showDatePickerDialog: 選擇的日期為 " + editText.getText().toString());
                }, year, month, dayOfMonth);
        datePickerDialog.show();
    }

    private void navigateToLogin() {
        Navigation.findNavController(requireView()).navigate(R.id.nav_login);
        Log.d("UserDataFragment", "navigateToLogin: 導航到登錄頁面");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        Log.d("UserDataFragment", "onDestroyView: 清空 binding");
    }

    private void handleApiResponse(String response) {
        try {
            JSONObject jsonResponse = new JSONObject(response);
            String message = jsonResponse.getString("message");
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            Log.d("UserDataFragment", "handleApiResponse: 處理 API 響應成功，消息：" + message);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "api 無法使用", Toast.LENGTH_SHORT).show();
            Log.e("UserDataFragment", "handleApiResponse: 處理 API 響應時發生 JSON 解析錯誤");
        }
    }
}
