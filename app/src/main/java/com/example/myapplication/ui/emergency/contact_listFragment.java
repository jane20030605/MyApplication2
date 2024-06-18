package com.example.myapplication.ui.emergency;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.myapplication.R;
import com.example.myapplication.utils.NetworkRequestManager;
import com.example.myapplication.utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class contact_listFragment extends Fragment {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<JSONObject> contactList;
    private ContactAdapter contactAdapter;
    private SessionManager sessionManager;
    private Button backButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 加載布局文件
        View root = inflater.inflate(R.layout.fragment_contact_list, container, false);

        // 初始化視圖元素
        recyclerView = root.findViewById(R.id.recyclerView);
        swipeRefreshLayout = root.findViewById(R.id.swipeRefreshLayout);
        backButton = root.findViewById(R.id.backbutton); // 新增回到個人檔案按鈕
        contactList = new ArrayList<>();
        contactAdapter = new ContactAdapter(contactList);
        // 設置 RecyclerView 的佈局管理器和適配器
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(contactAdapter);

        sessionManager = new SessionManager(requireContext());

        // 設置下拉刷新事件
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (sessionManager.isLoggedIn()) {
                    String account = sessionManager.getCurrentLoggedInAccount();
                    fetchContacts(account);
                } else {
                    navigateToLogin();
                }
            }
        });

        // 檢查用戶登錄狀態
        if (sessionManager.isLoggedIn()) {
            String account = sessionManager.getCurrentLoggedInAccount();
            fetchContacts(account);
        } else {
            navigateToLogin();
        }

        // 設置回到個人檔案按鈕的點擊事件
        backButton.setOnClickListener(v -> {
            // 導航到個人檔案介面
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.nav_user_data);
        });
        return root;
    }
    // 檢查用戶是否已登錄
    private boolean isLoggedIn() {
        // 在此實現檢查用戶登錄狀態的邏輯
        // 返回 true 表示已登錄，返回 false 表示尚未登錄
        // 這裡先假設用戶已登錄，實際情況需要根據你的應用邏輯來確定
        return true;
    }

    // 获取当前已登录用户的帐户
    private String getCurrentLoggedInAccount() {
        // 在此实现获取当前已登录用户帐户的逻辑
        // 返回当前已登录用户的帐户
        return "ACCOUNT";
    }
    private void fetchContacts(String account) {
        String phpApiUrl = "http://100.96.1.3/api_get_contact.php" + "?account=" + account;

        NetworkRequestManager.getInstance(getContext()).makeGetRequest(phpApiUrl, new NetworkRequestManager.RequestListener() {
            @Override
            public void onSuccess(String response) {
                displayContacts(response); // 成功获取联络人数据后显示
                swipeRefreshLayout.setRefreshing(false); // 停止刷新动画
            }

            @Override
            public void onError(String error) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show(); // 获取联络人数据失败时显示错误消息
                swipeRefreshLayout.setRefreshing(false); // 停止刷新动画
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void displayContacts(String contactData) {
        try {
            JSONArray contactsArray = new JSONArray(contactData);
            Log.e("ContactResponse", contactData);
            contactList.clear(); // 清空已有的联络人列表
            for (int i = 0; i < contactsArray.length(); i++) {
                JSONObject contact = contactsArray.getJSONObject(i);
                contactList.add(contact); // 将联络人加入到列表中
            }
            contactAdapter.notifyDataSetChanged(); // 通知 RecyclerView 更新
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void onContactSaved(String contact) {
        try {
            JSONObject newContact = new JSONObject(contact);
            addContact(newContact); // 添加新联络人到列表
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void addContact(JSONObject contact) {
        contactList.add(contact); // 将联络人加入到列表中
        contactAdapter.notifyDataSetChanged(); // 通知适配器数据集已更改
    }

    private void navigateToLogin() {
        Toast.makeText(getContext(), "登入後即可查看緊急連絡人\n請先進行登入", Toast.LENGTH_SHORT).show();
    }
}
