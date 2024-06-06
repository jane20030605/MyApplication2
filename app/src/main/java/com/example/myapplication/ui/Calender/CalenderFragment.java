package com.example.myapplication.ui.Calender;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.utils.NetworkRequestManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class CalenderFragment extends Fragment implements CalenderThingFragment.OnEventSavedListener {

    private List<String> eventList; // 事件列表
    private RecyclerView recyclerView; // RecyclerView 用於顯示事件列表
    private EventAdapter eventAdapter; // 事件適配器

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_calender, container, false);

        recyclerView = root.findViewById(R.id.recyclerView); // 初始化 RecyclerView
        eventList = new ArrayList<>(); // 初始化事件列表
        eventAdapter = new EventAdapter(eventList); // 初始化事件適配器
        recyclerView.setAdapter(eventAdapter); // 設置適配器給 RecyclerView

        FloatingActionButton fabAddCircularButton = root.findViewById(R.id.fabAddEvent); // FloatingActionButton 用於添加新事件
        fabAddCircularButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.nav_calender_thing); // 點擊按鈕時導航到添加事件的界面
            }
        });
        // 檢查用戶登錄狀態
        if (isLoggedIn()) {
            // 如果已登錄，則取得日曆事件
            fetchCalendarEvents();
        } else {
            // 如果尚未登錄，則導航到登錄界面
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

    // 取得日曆事件
    private void fetchCalendarEvents() {
        String account = "USERNAME"; // 要获取日历数据的用户帐户
        String phpApiUrl = "http://100.96.1.3/api_get_calendar.php" + "?account=" + account;

        NetworkRequestManager.getInstance(getContext()).makeGetRequest(phpApiUrl, new NetworkRequestManager.RequestListener() {
            @Override
            public void onSuccess(String response) {
                // 成功获取日历数据后显示事件
                displayEvents(response);
            }

            @Override
            public void onError(String error) {
                // 获取日历数据失败时显示错误消息
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void displayEvents(String calendarData) {
        try {
            JSONArray eventsArray = new JSONArray(calendarData);
            eventList.clear(); // 清空已有的事件列表
            for (int i = 0; i < eventsArray.length(); i++) {
                String event = eventsArray.getString(i);
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
        // 添加新事件到事件列表
        addEvent(event);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void addEvent(String event) {
        eventList.add(event); // 将事件加入到事件列表中
        eventAdapter.notifyDataSetChanged(); // 通知适配器数据集已更改
    }

    private void navigateToLogin() {
        // 在此實現導航到登錄界面的邏輯
        Navigation.findNavController(requireView()).navigate(R.id.nav_login);
    }
}
