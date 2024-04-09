package com.example.myapplication.ui.calender_thing;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentCalenderThingBinding;

import java.util.HashSet;
import java.util.Set;

public class CalenderThingFragment extends Fragment {

    private FragmentCalenderThingBinding binding;
    private SharedPreferences sharedPreferences;
    String[] companionsList = {"家人", "朋友", "個人", "工作"}; // 預設的事件對象列表

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCalenderThingBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        sharedPreferences = requireActivity().getSharedPreferences
                ("MyCalendar", Context.MODE_PRIVATE); // 獲取共享首選項

        Set<String> companionsSet = sharedPreferences.getStringSet
                ("companionsList", null); // 從共享首選項中獲取事件對象列表

        if (companionsSet != null) {
            companionsList = companionsSet.toArray(new String[0]); // 如果存在，則將其轉換為字符串數組
        }

        setupDateSpinner(); // 設置日期選擇器
        setupTimeSpinner(); // 設置時間選擇器

        ArrayAdapter<String> companionsAdapter = new ArrayAdapter<>
                (requireActivity(), android.R.layout.simple_spinner_item, companionsList); // 創建事件對象列表的適配器

        binding.spinnerCompanions.setAdapter(companionsAdapter); // 設置事件對象列表的適配器

        binding.saveButton.setOnClickListener(view -> saveEvent()); // 當按下保存按鈕時的操作

        return root; // 返回根視圖
    }

    private void setupDateSpinner() {
        // 不需要設置 Spinner Adapter，因為已經使用 DatePicker 來選擇日期
    }

    private void setupTimeSpinner() {
        // 不需要設置 Spinner Adapter，因為已經使用 TimePicker 來選擇時間
    }

    @SuppressLint({"DefaultLocale", "MutatingSharedPrefs"})
    private void saveEvent() {
        // 從日期選擇器獲取開始日期
        String startDate = getDateFromDatePicker(binding.datePickerStartDate);
        // 從日期選擇器獲取結束日期
        String endDate = getDateFromDatePicker(binding.datePickerEndDate);
        // 從時間選擇器獲取開始時間
        String startTime = getTimeFromTimePicker(binding.timePickerStartTime);
        // 從時間選擇器獲取結束時間
        String endTime = getTimeFromTimePicker(binding.timePickerEndTime);

        // 獲取共享首選項的編輯器
        SharedPreferences.Editor editor = sharedPreferences.edit();
        // 構建事件字符串
        String eventString = startDate + ";" + endDate + ";" + startTime;
        // 從共享首選項中獲取事件集合，如果不存在則創建一個空集合
        Set<String> eventsSet = sharedPreferences.getStringSet("events", new HashSet<>());
        // 將新事件添加到事件集合中
        eventsSet.add(eventString);
        // 將更新後的事件集合保存回共享首選項
        editor.putStringSet("events", eventsSet);
        editor.apply(); // 提交編輯

        // 創建Bundle對象，用於保存事件數據
        Bundle eventData = new Bundle();
        // 添加開始日期到Bundle中
        eventData.putString("startDate", startDate);
        // 添加結束日期到Bundle中
        eventData.putString("endDate", endDate);
        // 添加開始時間到Bundle中
        eventData.putString("startTime", startTime);
        // 添加結束時間到Bundle中
        eventData.putString("endTime", endTime);
        // 將事件數據發送給父片段
        getParentFragmentManager().setFragmentResult("eventData", eventData);

        Navigation.findNavController(requireView()).navigateUp(); // 導航回上一個目的地
    }

    @SuppressLint("DefaultLocale")
    private String getDateFromDatePicker(DatePicker datePicker) {
        int year = datePicker.getYear(); // 獲取年份
        int month = datePicker.getMonth() + 1; // 獲取月份，注意月份從0開始
        int day = datePicker.getDayOfMonth(); // 獲取日期
        return String.format("%04d-%02d-%02d", year, month, day); // 格式化日期為字符串
    }

    @SuppressLint("DefaultLocale")
    private String getTimeFromTimePicker(TimePicker timePicker) {
        int hour = timePicker.getHour(); // 獲取小時
        int minute = timePicker.getMinute(); // 獲取分鐘
        return String.format("%02d:%02d", hour, minute); // 格式化時間為字符串
    }
}
