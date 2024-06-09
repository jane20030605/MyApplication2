package com.example.myapplication.ui.user_data;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentUserDataBinding;
import com.example.myapplication.ui.emergency.EmergencyContactViewModel;

import java.util.Calendar;

public class user_dataFragment extends Fragment {

    private FragmentUserDataBinding binding;
    private TextView textViewEmergencyContact;
    private TextView textViewPhone;
    private TextView textViewRelationship;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentUserDataBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // 初始化按鈕元素
        Button buttonEmergencyContact = binding.buttonEmergencyContact;
        Button buttonCancel = binding.buttonCancel;
        Button buttonSave = binding.buttonDataSave;

        // 點擊保存按钮事件
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 獲取用戶輸入數據
                String username = binding.editTextUsername.getText().toString();
                String email = binding.editTextEmail.getText().toString();
                String phone = binding.editTextPhone.getText().toString();
                String address = binding.editTextAddress.getText().toString();
                String birthday = binding.editTextBirthday.getText().toString();

                // 將輸入內容暫時儲存在Bundle中
                Bundle bundle = new Bundle();
                bundle.putString("username", username);
                bundle.putString("email", email);
                bundle.putString("phone", phone);
                bundle.putString("address", address);
                bundle.putString("birthday", birthday);
                // 將Bundle設置給目標Fragment
                getParentFragmentManager().setFragmentResult("userData", bundle);
                // 確保所有字段都已填寫
                if (username.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty() || birthday.isEmpty()) {
                    Toast.makeText(requireContext(), "請填寫所有資料", Toast.LENGTH_SHORT).show();
                    return;
                }
                // 檢查郵件地址格式是否正確
                if (!isValidEmail(email)) {
                    Toast.makeText(requireContext(), "郵件地址格式不正確", Toast.LENGTH_SHORT).show();
                    return;
                }
                // 保存數據到SharedPreferences
                SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("REAL NAME", username);
                editor.putString("EMAIL", email);
                editor.putString("PHONE", phone);
                editor.putString("ADDRESS", address);
                editor.putString("BIRTHDAY", birthday);
                editor.apply();

                // 顯示保存成功的Toast
                Toast.makeText(requireContext(), "保存成功", Toast.LENGTH_SHORT).show();
                // 返回上一個介面
                Navigation.findNavController(v).navigateUp();
            }
        });

        // 點擊緊急連絡人按鈕事件
        buttonEmergencyContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 顯示提示消息並導航到緊急連絡人 Fragment
                Toast.makeText(requireContext(), "新增緊急連絡人", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(v).navigate(R.id.nav_emergency_contact);
            }
        });

        // 獲取 EmergencyContactViewModel 的實例
        EmergencyContactViewModel emergencyContactViewModel = new ViewModelProvider(requireActivity())
                .get(EmergencyContactViewModel.class);

        // 觀察緊急聯絡人信息的變化並更新界面
        emergencyContactViewModel.getEmergencyName().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String Name) {
                textViewEmergencyContact.setText(Name);
            }
        });

        // 觀察電話信息的變化並更新界面
        emergencyContactViewModel.getPhoneNumber().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String phone) {
                textViewPhone.setText(phone);
            }
        });

        // 觀察緊急聯絡人關係的變化並更新界面
        emergencyContactViewModel.getRelationship().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String relationship) {
                textViewRelationship.setText(relationship);
            }
        });

        // 點擊取消按钮事件
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 提示進行帳戶密碼變更修改
                Toast.makeText(requireContext(), "進行變更密碼", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(v).navigate(R.id.nav_password_change);
            }
        });

        // 在這裡添加設置EditText的輸入監聽器的代碼
        binding.editTextUsername.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    // 當按下Enter/下一步鍵時，將焦點轉移到下一個EditText
                    binding.editTextEmail.requestFocus();
                    return true;
                }
                return false;
            }
        });
        // 設置生日選擇器
        binding.editTextBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int mYear = calendar.get(Calendar.YEAR);
                int mMonth = calendar.get(Calendar.MONTH);
                int mDay = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // 將年、月、日組合成日期字串，這裡假設您希望日期格式為YYYY/MM/DD
                                @SuppressLint("DefaultLocale")
                                String formattedDate = String.format("%04d/%02d/%02d", year, (monthOfYear + 1), dayOfMonth);
                                binding.editTextBirthday.setText(formattedDate);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        // 從SharedPreferences中讀取用戶信息並設置到相應的視圖中
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        String real_name = sharedPreferences.getString("REAL NAME", "");
        String email = sharedPreferences.getString("EMAIL", "");
        String phone = sharedPreferences.getString("PHONE", "");
        String address = sharedPreferences.getString("ADDRESS", "");
        String birthday = sharedPreferences.getString("BIRTHDAY", "");

        binding.editTextUsername.setText(real_name);
        binding.editTextEmail.setText(email);
        binding.editTextPhone.setText(phone);
        binding.editTextAddress.setText(address);
        binding.editTextBirthday.setText(birthday);

        return root;
    }

    // 檢查郵件地址格式是否正確的方法
    private boolean isValidEmail(String email) {
        // 使用正則表達式來檢查郵件地址格式
        // 這個正則表達式僅供參考，實際使用中可以根據需求進行修改
        String emailPattern = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}";
        return email.matches(emailPattern);
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // 顯示編輯刪除對話框
    private void showEditDeleteDialog(final String eventText, View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("選擇操作")
                .setMessage("您要編輯還是刪除此聯絡人?")
                .setPositiveButton("編輯", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 在這裡處理編輯事件的邏輯
                        // 跳轉為編輯界面，執行其他編輯操作
                        Toast.makeText(requireContext(), "編輯緊急聯絡人", Toast.LENGTH_SHORT).show();

                        // 創建 Bundle 將文本資料傳遞到目標 fragment
                        Bundle bundle = new Bundle();
                        bundle.putString("eventName", eventText);
                        bundle.putString("eventPhone", eventText);
                        bundle.putString("eventContact", eventText);

                        // 導航到目標 fragment 並傳遞 Bundle
                        Navigation.findNavController(v).navigate(R.id.nav_emergency_contact, bundle);
                    }
                })

                .setNegativeButton("刪除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 在這裡處理刪除事件的邏輯
                        // 可以從數據庫中刪除該事件或執行其他刪除操作
                        Toast.makeText(requireContext(), "刪除緊急聯絡人" , Toast.LENGTH_SHORT).show();
                    }
                })
                .setNeutralButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 取消操作，不執行任何操作
                    }
                })
                .create()
                .show();
    }
}
