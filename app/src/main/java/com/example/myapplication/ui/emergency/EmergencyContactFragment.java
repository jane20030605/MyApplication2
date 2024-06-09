package com.example.myapplication.ui.emergency;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentEmergencyContactBinding;

import org.json.JSONArray;
import org.json.JSONObject;

public class EmergencyContactFragment extends Fragment {

    private FragmentEmergencyContactBinding binding;
    private EmergencyContactViewModel mViewModel;
    private boolean isEditing = false;
    private String contactId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentEmergencyContactBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // 初始化视图元素
        EditText editEmergencyName = binding.editEmergencyName;
        EditText editTextPhone = binding.editTextPhone;
        Spinner spinnerContact = binding.spinnerContact;
        Button buttonSave = binding.buttonSave;
        Button buttonCancel = binding.buttonCancel;

        // 初始化 ViewModel
        mViewModel = new ViewModelProvider(requireActivity()).get(EmergencyContactViewModel.class);

        // 检查是否有联系人 ID
        if (getArguments() != null) {
            contactId = getArguments().getString("contactId");
            if (contactId != null && !contactId.isEmpty()) {
                isEditing = true;
                loadContactDetails(contactId);
            }
        }

        // 设置下拉菜单项目
        setSpinnerItems(spinnerContact, R.array.contact);

        // 保存按钮的点击事件
        buttonSave.setOnClickListener(v -> {
            // 获取用户输入的紧急联系人信息
            String emergencyName = editEmergencyName.getText().toString().trim();
            String phoneNumber = editTextPhone.getText().toString().trim();
            String relationship = spinnerContact.getSelectedItem().toString();
            String accountId = "Account"; // 使用者账号ID，根据实际情况设置

            // 确保所有字段都已填写
            if (emergencyName.isEmpty() || phoneNumber.isEmpty()) {
                Toast.makeText(requireContext(), "請填寫所有資料", Toast.LENGTH_SHORT).show();
                return;
            }

            if (isEditing) {
                // 编辑模式
                new Thread(() -> {
                    try {
                        String result = ContactUpdateClient.updateContact(contactId, emergencyName, phoneNumber, relationship, accountId);
                        getActivity().runOnUiThread(() -> {
                            Toast.makeText(requireContext(), result, Toast.LENGTH_SHORT).show();
                            if (result.equals("修改成功")) {
                                Navigation.findNavController(v).navigate(R.id.nav_contact_list);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        getActivity().runOnUiThread(() -> Toast.makeText(requireContext(), "修改緊急連絡人出錯 " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    }
                }).start();
            } else {
                // 新增模式
                new Thread(() -> {
                    try {
                        String result = ContactAddClient.addContact(emergencyName, phoneNumber, relationship, accountId);
                        getActivity().runOnUiThread(() -> {
                            Toast.makeText(requireContext(), result, Toast.LENGTH_SHORT).show();
                            if (result.equals("新增成功")) {
                                Navigation.findNavController(v).navigate(R.id.nav_contact_list);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        getActivity().runOnUiThread(() -> Toast.makeText(requireContext(), "新增緊急連絡人出錯: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    }
                }).start();
            }
        });

        // 取消按钮的点击事件
        buttonCancel.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "取消新增緊急連絡人", Toast.LENGTH_SHORT).show();
            Navigation.findNavController(v).navigateUp();
        });

        // 在这里添加设置EditText的输入监听器的代码
        editEmergencyName.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                // 当按下Enter/下一步键时，将焦点转移到下一个EditText
                editTextPhone.requestFocus();
                return true;
            }
            return false;
        });

        return root;
    }

    // 设置下拉菜单项目
    private void setSpinnerItems(Spinner spinner, int arrayId) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(),
                arrayId, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    // 根据值获取 spinner 中对应的位置
    private int getPositionByValue(Spinner spinner, String value) {
        ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) spinner.getAdapter();
        return adapter.getPosition(value);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void loadContactDetails(String contactId) {
        new Thread(() -> {
            try {
                String accountId = "Account"; // 使用者账号ID，根据实际情况设置
                String response = ContactGetClient.getContact(accountId);
                JSONArray contactArray = new JSONArray(response);

                for (int i = 0; i < contactArray.length(); i++) {
                    JSONObject contact = contactArray.getJSONObject(i);
                    if (contactId.equals(contact.optString("contact_Id"))) {
                        getActivity().runOnUiThread(() -> {
                            binding.editEmergencyName.setText(contact.optString("contact_name"));
                            binding.editTextPhone.setText(contact.optString("contact_tel"));
                            int position = getPositionByValue(binding.spinnerContact, contact.optString("relation"));
                            binding.spinnerContact.setSelection(position);
                        });
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                getActivity().runOnUiThread(() -> Toast.makeText(requireContext(), "获取联系人详情时出错: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}
