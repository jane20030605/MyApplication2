package com.example.myapplication.ui.Calender;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.myapplication.R;
import com.example.myapplication.utils.NetworkRequestManager;
import com.example.myapplication.utils.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CalenderFragment extends Fragment implements CalenderThingFragment.OnEventSavedListener {

    private List<JSONObject> eventList; // 事件列表
    private RecyclerView recyclerView; // RecyclerView 用於顯示事件列表
    private EventAdapter eventAdapter; // 事件適配器
    private SwipeRefreshLayout swipeRefreshLayout; // 用於下拉刷新的 SwipeRefreshLayout
    private SessionManager sessionManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_calender, container, false);

        recyclerView = root.findViewById(R.id.recyclerView); // 初始化 RecyclerView
        swipeRefreshLayout = root.findViewById(R.id.swipeRefreshLayout); // 初始化 SwipeRefreshLayout
        eventList = new ArrayList<>(); // 初始化事件列表
        eventAdapter = new EventAdapter(eventList); // 初始化事件適配器
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(eventAdapter); // 設置適配器給 RecyclerView

        FloatingActionButton fabAddCircularButton = root.findViewById(R.id.fabAddEvent); // FloatingActionButton 用於添加新事件
        fabAddCircularButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.nav_calender_thing); // 點擊按鈕時導航到添加事件的界面
            }
        });

        sessionManager = new SessionManager(requireContext());

        // 設置下拉刷新動作
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (sessionManager.isLoggedIn()) {
                    String account = sessionManager.getCurrentLoggedInAccount();
                    fetchCalendarEvents(account);
                } else {
                    navigateToLogin();
                }
            }
        });

        // 檢查用戶登錄狀態
        if (sessionManager.isLoggedIn()) {
            String account = sessionManager.getCurrentLoggedInAccount();
            fetchCalendarEvents(account);
        } else {
            navigateToLogin();
        }

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

    // 取得日曆事件
    private void fetchCalendarEvents(String account) {
        String phpApiUrl = "http://100.96.1.3/api_get_calendar.php" + "?account=" + account;

        NetworkRequestManager.getInstance(getContext()).makeGetRequest(phpApiUrl, new NetworkRequestManager.RequestListener() {
            @Override
            public void onSuccess(String response) {
                displayEvents(response); // 成功获取日历数据后显示事件
                swipeRefreshLayout.setRefreshing(false); // 停止刷新动画
            }

            @Override
            public void onError(String error) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show(); // 获取日历数据失败时显示错误消息
                swipeRefreshLayout.setRefreshing(false); // 停止刷新动画
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void displayEvents(String calendarData) {
        try {
            JSONArray eventsArray = new JSONArray(calendarData);
            Log.e("CalendarResponse",  calendarData);
            eventList.clear(); // 清空已有的事件列表
            for (int i = 0; i < eventsArray.length(); i++) {
                JSONObject event = eventsArray.getJSONObject(i);
                eventList.add(event); // 将事件加入到事件列表中
            }
            eventAdapter.notifyDataSetChanged(); // 通知 RecyclerView 更新
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // 實現 OnEventSavedListener 接口的方法
    @Override
    public void onEventSaved(String event) {
        try {
            JSONObject newEvent = new JSONObject(event);
            addEvent(newEvent); // 添加新事件到事件列表
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void addEvent(JSONObject event) {
        eventList.add(event); // 将事件加入到事件列表中
        eventAdapter.notifyDataSetChanged(); // 通知适配器数据集已更改
    }

    private void navigateToLogin() {
        // 在此實現導航到登錄界面的邏輯
        Navigation.findNavController(requireView()).navigate(R.id.nav_login);
    }
}