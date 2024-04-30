package com.example.myapplication.ui.user_data;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
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

import java.util.ArrayList;
import java.util.List;
public class user_dataFragment extends Fragment {

    private FragmentUserDataBinding binding;
    private TextView textViewEmergencyContact;
    private TextView textViewPhone;
    private TextView textViewRelationship;
    private List<TextView> eventTextViews; // 新增一個列表來存儲事件列表中的文本視圖

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentUserDataBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // 初始化文本視圖元素
        textViewEmergencyContact = binding.eventTextView0;
        textViewPhone = binding.eventTextView1;
        textViewRelationship = binding.eventTextView2;

        // 初始化事件列表中的文本視圖列表
        eventTextViews = new ArrayList<>();
        eventTextViews.add(textViewEmergencyContact);
        eventTextViews.add(textViewPhone);
        eventTextViews.add(textViewRelationship);

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
                String emergencyContact = textViewEmergencyContact.getText().toString();
                // 將輸入內容暫時儲存在Bundle中
                Bundle bundle = new Bundle();
                bundle.putString("username", username);
                bundle.putString("email", email);
                bundle.putString("phone", phone);
                bundle.putString("address", address);
                bundle.putString("emergencyContact", emergencyContact);
                // 將Bundle設置給目標Fragment
                getParentFragmentManager().setFragmentResult("userData", bundle);
                // 確保所有字段都已填寫
                if (username.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty() || emergencyContact.isEmpty()) {
                    Toast.makeText(requireContext(), "請填寫所有資料", Toast.LENGTH_SHORT).show();
                    return;
                }
                // 當使用者輸入的資料保存成功時，可以將資料存儲到資料庫或其他持久化儲存裝置中。
                // 在這個示例中，我們簡單地顯示一個 Toast 訊息表示保存成功。
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

        // 對每個事件文本視圖添加點擊監聽器，以實現修改和刪除功能
        for (final TextView eventTextView : eventTextViews) {
            eventTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 顯示對話框，讓用戶選擇編輯或刪除該事件
                    showEditDeleteDialog(eventTextView.getText().toString(), v);
                }
            });
        }

        // 點擊取消按钮事件
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 提示用戶取消個人資料修改
                Toast.makeText(requireContext(), "取消個人資料修改", Toast.LENGTH_SHORT).show();
                // 返回上一個介面
                Navigation.findNavController(v).navigateUp();
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

        return root;
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
