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
    // 取得日曆事件
    private void fetchCalendarEvents() {
        String account = "USERNAME"; // 要獲取日曆資料的用戶帳戶
        CalendarApiClient.fetchCalendarData(new CalendarApiClient.Callback() {
            @Override
            public void onSuccess(String result) {
                // 成功取得日曆資料後顯示事件
                displayEvents(result);
            }

            @Override
            public void onFailure(final String errorMessage) {
                // 取得日曆資料失敗時顯示錯誤訊息
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onDataReceived(String calendarData) {
                // 在此方法中處理收到的日曆資料
                displayEvents(calendarData);
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
                eventList.add(event); // 將事件加入到事件列表中
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
        eventList.add(event); // 將事件加入到事件列表中
        eventAdapter.notifyDataSetChanged(); // 通知適配器數據集已更改
    }
    private void navigateToLogin() {
        // 在此實現導航到登錄界面的邏輯
        Navigation.findNavController(requireView()).navigate(R.id.nav_login);
    }

}
