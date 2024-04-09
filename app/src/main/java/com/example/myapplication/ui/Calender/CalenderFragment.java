package com.example.myapplication.ui.Calender;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentCalenderBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class CalenderFragment extends Fragment {

    private TextView eventTextView; // 顯示事件的TextView
    private int selectedYear; // 選擇的年份
    private int selectedMonth; // 選擇的月份
    private int selectedDay; // 選擇的日期

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentCalenderBinding binding = FragmentCalenderBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        eventTextView = binding.eventTextView; // 獲取事件TextView
        FloatingActionButton fabAddEvent = binding.fabAddEvent; // 添加事件的浮動操作按鈕
        DatePicker datePicker = binding.datePicker; // 日曆日期選擇器

        // 設置日期選擇器的選擇監聽器
        datePicker.init(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(),
                (view, year, monthOfYear, dayOfMonth) -> {
            selectedYear = year; // 更新選擇的年份
            selectedMonth = monthOfYear; // 更新選擇的月份
            selectedDay = dayOfMonth; // 更新選擇的日期
            updateAddEventButtonDate(fabAddEvent); // 更新添加事件按鈕的日期
        });

        // 當按下添加事件按鈕時的操作
        fabAddEvent.setOnClickListener(view -> {
            try {
                Navigation.findNavController(view).navigate(R.id.nav_calender_thing); // 導航到添加事件的目的地
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(requireContext(), "出現錯誤，請再試一次", Toast.LENGTH_SHORT).show(); // 顯示錯誤消息
            }
        });

        displayEvents(); // 顯示事件
        return root; // 返回根視圖
    }

    // 更新添加事件按鈕的日期
    private void updateAddEventButtonDate(FloatingActionButton fabAddEvent) {
        String selectedDateText = String.format(Locale.getDefault(),
                "%d/%02d/%02d", selectedYear, selectedMonth + 1, selectedDay); // 格式化選擇的日期文字
        fabAddEvent.setContentDescription(selectedDateText); // 設置浮動操作按鈕的內容描述為選擇的日期文字
    }

    // 顯示事件
    private void displayEvents() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyCalendar", Context.MODE_PRIVATE); // 獲取共享首選項
        Set<String> eventsSet = sharedPreferences.getStringSet("events", new HashSet<>()); // 從共享首選項中獲取事件集合，如果不存在則返回空集合

        StringBuilder eventsText = new StringBuilder(); // 用於構建事件文字的字符串生成器
        for (String event : eventsSet) {
            String[] eventDetails = event.split(";"); // 拆分事件詳細信息
            String startDate = eventDetails[0]; // 開始日期
            String endDate = eventDetails[1]; // 結束日期
            String startTime = eventDetails[2]; // 開始時間
            String endTime = eventDetails[3]; // 結束時間
            String companions = eventDetails[4]; // 事件對象
            String description = eventDetails[5]; // 事件說明

            // 將事件詳細信息添加到事件文字中
            eventsText.append("起始日期: ").append(startDate).append("\n")
                    .append("結束日期: ").append(endDate).append("\n")
                    .append("起始時間: ").append(startTime).append("\n")
                    .append("結束時間: ").append(endTime).append("\n")
                    .append("事件對象: ").append(companions).append("\n")
                    .append("事件說明: ").append(description).append("\n\n");
        }
        eventTextView.setText(eventsText.toString()); // 將事件文字設置到事件TextView中
    }
}
