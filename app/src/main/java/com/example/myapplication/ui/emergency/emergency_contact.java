package com.example.myapplication.ui.emergency;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentEmergencyContactBinding;
import com.example.myapplication.ui.emergency.apiclient.ContactAddClient;
import com.example.myapplication.utils.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

public class emergency_contact extends Fragment {

    private FragmentEmergencyContactBinding binding;
    private ContactAddClient apiClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiClient = new ContactAddClient();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEmergencyContactBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // 初始化偏好設置和會話管理器
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        SessionManager sessionManager = new SessionManager(requireContext());

        // 點擊保存按鈕的動作
        binding.buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    saveEmergencyContact();
                    Toast.makeText(requireContext(), "保存成功，已上傳資料" , Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(v).navigate(R.id.nav_user_data);
                } catch (JSONException e) {
                    throw new RuntimeException(e);

                }
            }
        });

        // 點擊取消按鈕的動作
        binding.buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelAddingContact();
            }
        });


        // 初始化下拉式選單的選項
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.contact, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerContact.setAdapter(adapter);

        return root;
    }

    private void saveEmergencyContact() throws JSONException {
        // 獲取輸入的緊急連絡人信息
        String name = binding.editEmergencyName.getText().toString();
        String phone = binding.editTextPhone.getText().toString();
        String relationChinese = binding.spinnerContact.getSelectedItem().toString();
        String relationEnglish = mapChineseToEnglish(relationChinese);

        // 從 SharedPreferences 中讀取帳戶信息
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        String account = sharedPreferences.getString("ACCOUNT", "");

        // 創建 ContactEvent 對象並添加帳戶信息
        ContactEvent contactEvent = new ContactEvent(name, phone, relationEnglish, account);

        // 將 ContactEvent 對象轉換為 JSON 字符串
        String eventDataJson = createContactDataJson(contactEvent);

        // 使用 ContactAddClient 類中的 addContact 方法保存緊急聯絡人
        apiClient.addContact(eventDataJson, new ContactAddClient.ContactAddCallback() {
            @Override
            public void onSuccess(String message) {
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(requireContext(), "保存成功，已上傳資料" , Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(String message) {
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }
        });
    }

    private String createContactDataJson(ContactEvent contactEvent) throws JSONException {
        JSONObject eventDataJson = new JSONObject();
        eventDataJson.put("contact_name", contactEvent.getContactName());
        eventDataJson.put("contact_tel", contactEvent.getContactTel());
        eventDataJson.put("relation", contactEvent.getRelation());
        eventDataJson.put("account", contactEvent.getAccount());
        return eventDataJson.toString();
    }

    private void cancelAddingContact() {
        // 顯示取消提示
        Toast.makeText(getContext(), "取消新增連絡人", Toast.LENGTH_SHORT).show();

        // 導航到個人檔案介面
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
        navController.navigate(R.id.nav_user_data);
    }

    private String mapChineseToEnglish(String chinese) {
        switch (chinese) {
            case "請選擇":
                return ""; // 或者你可以根据业务逻辑返回适当的默认值
            case "兒女":
                return "children";
            case "親戚":
                return "relatives";
            case "配偶":
                return "spouse";
            case "朋友":
                return "friend";
            case "其他":
                return "other";
            default:
                return "";
        }
    }

    private String mapEnglishToChinese(String english) {
        switch (english) {
            case "":
                return "請選擇";
            case "children":
                return "兒女";
            case "relatives":
                return "親戚";
            case "spouse":
                return "配偶";
            case "friend":
                return "朋友";
            case "other":
                return "其他";
            default:
                return "";
        }
    }

    private void navigateToContactList() {
        SessionManager sessionManager = new SessionManager(requireContext());
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);

        if (sessionManager.isLoggedIn()) {
            // 已登入，導航到緊急聯絡人列表介面
            navController.navigate(R.id.nav_contact_list);
        } else {
            // 未登入，導航到登入介面
            navController.navigate(R.id.nav_login);
        }
    }
}
