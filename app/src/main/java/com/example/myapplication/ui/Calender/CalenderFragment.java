package com.example.myapplication.ui.Calender;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.myapplication.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.HashSet;
import java.util.Set;

public class CalenderFragment extends Fragment {

    private TextView eventTextView;
    private Set<String> eventsSet;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_calender, container, false);

        eventTextView = root.findViewById(R.id.eventTextView);

        FloatingActionButton fabAddCircularButton = root.findViewById(R.id.fabAddEvent);
        fabAddCircularButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.nav_calender_thing);
            }
        });

        eventTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditDeleteDialog(eventTextView.getText().toString(), v);
            }
        });

        displayEvents();

        return root;
    }

    private void displayEvents() {
        // 獲取 SharedPreferences 實例，用於讀取 "MyCalendar" 偏好設置文件
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyCalendar", Context.MODE_PRIVATE);
        // 從 SharedPreferences 中獲取名為 "events" 的字符串集合，如果沒有則返回一個新的空集合
        eventsSet = sharedPreferences.getStringSet("events", new HashSet<>());
        // 用於存儲所有事件的字符串構建器
        StringBuilder eventsText = new StringBuilder();
        // 遍歷集合中的每個事件，並將其添加到字符串構建器中，每個事件之間用兩個換行符分隔
        for (String event : eventsSet) {
            eventsText.append(event).append("\n\n");
        }
        // 將構建的字符串設置為 TextView 的文本
        eventTextView.setText(eventsText.toString());
    }


    private void showEditDeleteDialog(final String eventText, View v) {
        // 創建一個 AlertDialog.Builder 用於構建對話框
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

        // 設置對話框標題和訊息
        builder.setTitle("選擇操作")
                .setMessage("您要編輯還是刪除此事件?")
                // 設置編輯按鈕及其點擊事件處理邏輯
                .setPositiveButton("編輯", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 創建一個 Bundle 並放入事件細節和編輯狀態
                        Bundle bundle = new Bundle();
                        bundle.putString("eventDetails", eventText);
                        bundle.putBoolean("isEditing", true);
                        // 導航到編輯頁面並傳遞數據
                        Navigation.findNavController(requireView()).navigate(R.id.nav_calender_thing, bundle);
                    }
                })

                // 設置刪除按鈕及其點擊事件處理邏輯
                .setNegativeButton("刪除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 從事件集合中移除選中的事件
                        eventsSet.remove(eventText);
                        // 保存更新後的事件集合到 SharedPreferences
                        saveEventsToSharedPreferences();
                        // 更新顯示的事件列表
                        displayEvents();
                        // 顯示刪除成功提示
                        Toast.makeText(requireContext(), "事件已刪除", Toast.LENGTH_SHORT).show();
                    }
                })

                // 設置取消按鈕，取消按鈕無需額外處理邏輯
                .setNeutralButton("取消", null)
                // 創建並顯示對話框
                .create()
                .show();
    }

    private void saveEventsToSharedPreferences() {
        // 獲取 SharedPreferences 實例，用於讀寫 "MyCalendar" 偏好設置文件
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyCalendar", Context.MODE_PRIVATE);
        // 獲取 SharedPreferences 編輯器
        SharedPreferences.Editor editor = sharedPreferences.edit();
        // 將事件集合保存到 SharedPreferences
        editor.putStringSet("events", eventsSet);
        // 提交更改
        editor.apply();
    }
}