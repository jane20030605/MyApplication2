package com.example.myapplication.ui.calender_thing;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.databinding.FragmentCalenderThingBinding;

public class calender_thingFragment extends Fragment {

    private CalenderThingViewModel mViewModel; // 宣告 ViewModel 物件
    private FragmentCalenderThingBinding binding; // 宣告 Binding 物件

    // 創建視圖時呼叫
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // 使用 Binding 物件將佈局與 Fragment 綁定
        binding = FragmentCalenderThingBinding.inflate(inflater, container, false);
        View root = binding.getRoot(); // 獲取根視圖
        // 初始化界面元素
        final EditText editText_thing = binding.editTextThing;
        final EditText editTextEventDescription = binding.editTextEventDescription;
        final DatePicker datePickerStartDate = binding.datePickerStartDate;
        final DatePicker datePickerEndDate = binding.datePickerEndDate;
        final TimePicker timePickerStartTime = binding.timePickerStartTime;
        final TimePicker timePickerEndTime = binding.timePickerEndTime;
        final Button saveButton = binding.saveButton;
        final Button editButton = binding.editButton;


        // 保存按鈕點擊事件處理
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 獲取用戶輸入的事件信息
                String eventName = editText_thing.getText().toString();
                String eventDescription = editTextEventDescription.getText().toString();
                int startYear = datePickerStartDate.getYear();
                int startMonth = datePickerStartDate.getMonth();
                int startDay = datePickerStartDate.getDayOfMonth();
                int endYear = datePickerEndDate.getYear();
                int endMonth = datePickerEndDate.getMonth();
                int endDay = datePickerEndDate.getDayOfMonth();
                int startHour = timePickerStartTime.getCurrentHour();
                int startMinute = timePickerStartTime.getCurrentMinute();
                int endHour = timePickerEndTime.getCurrentHour();
                int endMinute = timePickerEndTime.getCurrentMinute();
                // 調用 ViewModel 的方法保存事件
                mViewModel.saveEvent(eventName, eventDescription,
                        startYear, startMonth, startDay, startHour, startMinute,
                        endYear, endMonth, endDay, endHour, endMinute);
            }
        });

        // 編輯按鈕點擊事件處理
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 獲取用戶輸入的事件信息
                String eventName = editText_thing.getText().toString();
                String eventDescription = editTextEventDescription.getText().toString();
                int startYear = datePickerStartDate.getYear();
                int startMonth = datePickerStartDate.getMonth();
                int startDay = datePickerStartDate.getDayOfMonth();
                int endYear = datePickerEndDate.getYear();
                int endMonth = datePickerEndDate.getMonth();
                int endDay = datePickerEndDate.getDayOfMonth();
                int startHour = timePickerStartTime.getCurrentHour();
                int startMinute = timePickerStartTime.getCurrentMinute();
                int endHour = timePickerEndTime.getCurrentHour();
                int endMinute = timePickerEndTime.getCurrentMinute();
                // 調用 ViewModel 的方法編輯事件
                mViewModel.editEvent(eventName, eventDescription,
                        startYear, startMonth, startDay, startHour, startMinute,
                        endYear, endMonth, endDay, endHour, endMinute);
            }
        });

        return root; // 返回根視圖
    }

    // 當 Activity 創建時呼叫
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(CalenderThingViewModel.class); // 獲取 ViewModel 物件
    }
}
