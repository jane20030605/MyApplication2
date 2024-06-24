package com.example.myapplication.ui.emergency;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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
import com.example.myapplication.ui.emergency.apiclient.ContactUpdateClient;
import com.example.myapplication.utils.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

public class Contact_updateFragment extends Fragment {

    private FragmentEmergencyContactBinding binding;
    private ContactUpdateClient apiClient;
    private final ContactUpdateEvent contactUpdateEvent = new ContactUpdateEvent();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiClient = new ContactUpdateClient();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEmergencyContactBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        SessionManager sessionManager = new SessionManager(requireContext());

        // 初始化下拉列表适配器
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.contact, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerContact.setAdapter(adapter);

        Bundle args = getArguments();
        if (args != null && args.containsKey("contact_Id")) {
            contactUpdateEvent.setContactId(args.getString("contact_Id"));
            contactUpdateEvent.setAccount(args.getString("account"));
            contactUpdateEvent.setContactName(args.getString("contact_name"));
            contactUpdateEvent.setContactTel(args.getString("contact_tel"));
            contactUpdateEvent.setContactMail(args.getString("contact_mail")); // 新增的email欄位
            contactUpdateEvent.setRelation(args.getString("relation"));

            Log.e("ContactUpdateEvent", contactUpdateEvent.getAllValue());

            binding.editEmergencyName.setText(contactUpdateEvent.getContactName());
            binding.editTextPhone.setText(contactUpdateEvent.getContactTel());
            binding.editTextEmail.setText(contactUpdateEvent.getContactMail()); // 設置email

            // 确保下拉列表适配器不为空后获取选项位置
            adapter = (ArrayAdapter<CharSequence>) binding.spinnerContact.getAdapter();
            int position = adapter.getPosition(mapEnglishToChinese(contactUpdateEvent.getRelation()));
            binding.spinnerContact.setSelection(position);
        }

        binding.buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateEmergencyContact();
                Navigation.findNavController(v).navigate(R.id.nav_contact_list);
                Toast.makeText(requireContext(), "更新成功，已上傳數據", Toast.LENGTH_SHORT).show();
            }
        });

        binding.buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "取消修改聯絡人", Toast.LENGTH_SHORT).show();
                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
                navController.navigate(R.id.nav_contact_list);
            }
        });

        return root;
    }

    private void updateEmergencyContact() {
        // 如果 contactUpdateEvent 为 null，直接返回
        if (contactUpdateEvent == null) {
            return;
        }

        String name = binding.editEmergencyName.getText().toString();
        String phone = binding.editTextPhone.getText().toString();
        String mail = binding.editTextEmail.getText().toString(); // 新增的email欄位
        String relation = mapChineseToEnglish(binding.spinnerContact.getSelectedItem().toString());

        // 从 SharedPreferences 中读取账户信息
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        String account = sharedPreferences.getString("ACCOUNT", "");

        contactUpdateEvent.setContactName(name);
        contactUpdateEvent.setContactTel(phone);
        contactUpdateEvent.setContactMail(mail); // 設置email
        contactUpdateEvent.setRelation(relation);
        contactUpdateEvent.setAccount(account);

        try {
            String eventDataJson = createContactDataJson(contactUpdateEvent);
            Log.d("eventDataJson", eventDataJson); // 调试日志，确保 eventDataJson 正确构建

            apiClient.updateContact(eventDataJson, new ContactUpdateClient.ContactUpdateCallback() {
                @Override
                public void onSuccess(String message) {
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(requireContext(), "保存成功，已上传数据", Toast.LENGTH_SHORT).show();
                        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
                        navController.navigate(R.id.nav_contact_list);
                    });
                }

                @Override
                public void onError(String message) {
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(requireContext(), "保存失败：" + message, Toast.LENGTH_SHORT).show();
                    });
                }
            });
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private String createContactDataJson(ContactUpdateEvent contactUpdateEvent) throws JSONException {
        JSONObject eventDataJson = new JSONObject();
        eventDataJson.put("contact_Id", contactUpdateEvent.getContactId());
        eventDataJson.put("contact_name", contactUpdateEvent.getContactName());
        eventDataJson.put("contact_tel", contactUpdateEvent.getContactTel());
        eventDataJson.put("contact_mail", contactUpdateEvent.getContactMail()); // 新增的email欄位
        eventDataJson.put("relation", contactUpdateEvent.getRelation());
        eventDataJson.put("account", contactUpdateEvent.getAccount());
        return eventDataJson.toString();
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
                return ""; // 或者处理未知情况的默认值
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
                return ""; // 或者处理未知情况的默认值
        }
    }
}
