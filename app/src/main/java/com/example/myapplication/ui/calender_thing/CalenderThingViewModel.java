package com.example.myapplication.ui.calender_thing;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Toast;

import androidx.lifecycle.ViewModel;
import androidx.navigation.Navigation;

import com.example.myapplication.R;

import java.util.HashSet;
import java.util.Set;

public class CalenderThingViewModel extends ViewModel {

    // 儲存事件
    @SuppressLint("MutatingSharedPrefs")
    public void saveEvent(Context context, String eventName, String eventDescription,
                          int startYear, int startMonth, int startDay,
                          int startHour, int startMinute,
                          int endYear, int endMonth, int endDay,
                          int endHour, int endMinute, View v) {

        String eventDetails = generateEventDetails(eventName, eventDescription,
                startYear, startMonth, startDay, startHour, startMinute,
                endYear, endMonth, endDay, endHour, endMinute);

        // 取得共用偏好設定
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyCalendar", Context.MODE_PRIVATE);
        // 取得已儲存的事件集合，若無則新建一個空集合
        Set<String> eventsSet = sharedPreferences.getStringSet("events", new HashSet<>());
        // 將新事件加入集合
        eventsSet.add(eventDetails);

        // 編輯共用偏好設定中的事件集合
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet("events", eventsSet);
        editor.apply();

        // 顯示儲存成功訊息
        Toast.makeText(context, "事件已儲存", Toast.LENGTH_SHORT).show();

        // 導航到日曆介面
        Navigation.findNavController(v).navigate(R.id.nav_calender);
    }


    // 編輯事件
    @SuppressLint("MutatingSharedPrefs")
    public void editEvent(Context context, String eventName, String eventDescription,
                          int startYear, int startMonth, int startDay,
                          int startHour, int startMinute,
                          int endYear, int endMonth, int endDay,
                          int endHour, int endMinute, View v) {

        String eventDetails = generateEventDetails(eventName, eventDescription,
                startYear, startMonth, startDay, startHour, startMinute,
                endYear, endMonth, endDay, endHour, endMinute);

        // 取得共用偏好設定
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyCalendar", Context.MODE_PRIVATE);
        // 取得已儲存的事件集合，若無則新建一個空集合
        Set<String> eventsSet = sharedPreferences.getStringSet("events", new HashSet<>());

        // 如果存在相同的事件詳細資訊，則先移除舊的事件詳細資訊
        eventsSet.remove(eventDetails);
        // 添加更新後的事件詳細資訊
        eventsSet.add(eventDetails);

        // 編輯共用偏好設定中的事件集合
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet("events", eventsSet);
        editor.apply();

        // 顯示更新成功訊息
        Toast.makeText(context, "事件已更新", Toast.LENGTH_SHORT).show();

        // 導航回到日曆介面
        Navigation.findNavController(v).navigate(R.id.nav_calender);
    }

    // 清除事件
    public void clearEvents(Context context) {
        // 获取SharedPreferences实例
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyCalendar", Context.MODE_PRIVATE);

        // 获取SharedPreferences.Editor实例
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // 清除事件数据
        editor.clear();
        editor.apply(); // 应用更改
    }

    // 生成事件詳細資訊字串
    private String generateEventDetails(String eventName, String eventDescription,
                                        int startYear, int startMonth, int startDay,
                                        int startHour, int startMinute,
                                        int endYear, int endMonth, int endDay,
                                        int endHour, int endMinute) {

        // 根據您的應用邏輯，將事件詳細資訊組合成字串並返回
        String eventDetailsBuilder =
                "事件名稱: " + eventName + "\n" +
                        "事件描述: " + eventDescription + "\n" +
                        "起始日期: " + startYear + "/" + startMonth + "/" + startDay + "\n" +
                        "結束日期: " + endYear + "/" + endMonth + "/" + endDay + "\n" +
                        "起始時間: " + startHour + ":" + startMinute + "\n" +
                        "結束時間: " + endHour + ":" + endMinute + "\n";

        return eventDetailsBuilder;
    }
}
