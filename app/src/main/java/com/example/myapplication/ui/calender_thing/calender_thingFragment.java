package com.example.myapplication.ui.calender_thing;

import android.os.Bundle; // 匯入Bundle類別
import android.view.LayoutInflater; // 匯入LayoutInflater類別
import android.view.View; // 匯入View類別
import android.view.ViewGroup; // 匯入ViewGroup類別
import android.widget.Button; // 匯入Button類別
import android.widget.DatePicker; // 匯入DatePicker類別
import android.widget.EditText; // 匯入EditText類別
import android.widget.TimePicker; // 匯入TimePicker類別

import androidx.annotation.NonNull; // 匯入NonNull注解
import androidx.annotation.Nullable; // 匯入Nullable注解
import androidx.fragment.app.Fragment; // 匯入Fragment類別
import androidx.lifecycle.ViewModelProvider; // 匯入ViewModelProvider類別

import com.example.myapplication.databinding.FragmentCalenderThingBinding; // 匯入FragmentCalenderThingBinding類別

public class calender_thingFragment extends Fragment { // 日曆事項Fragment類別定義

    private CalenderThingViewModel mViewModel; // 宣告 ViewModel 對象
    private FragmentCalenderThingBinding binding; // 宣告 Binding 對象

    public View onCreateView(@NonNull LayoutInflater inflater, // 創建視圖時調用
                             ViewGroup container, Bundle savedInstanceState) { // onCreateView方法定義
        binding = FragmentCalenderThingBinding.inflate(inflater, container, false); // 使用 Binding 對象將布局與 Fragment 綁定
        View root = binding.getRoot(); // 獲取根視圖

        final EditText editTextthing = binding.editTextthing; // 初始化界面元素
        final EditText editTextEventDescription = binding.editTextEventDescription;
        final DatePicker datePickerStartDate = binding.datePickerStartDate;
        final DatePicker datePickerEndDate = binding.datePickerEndDate;
        final TimePicker timePickerStartTime = binding.timePickerStartTime;
        final TimePicker timePickerEndTime = binding.timePickerEndTime;
        final Button saveButton = binding.saveButton;
        final Button editButton = binding.editButton;

        saveButton.setOnClickListener(new View.OnClickListener() { // 保存按鈕點擊事件處理
            @Override
            public void onClick(View v) { // onClick方法定義
                String eventName = editTextthing.getText().toString(); // 獲取用戶輸入的事件信息
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

                mViewModel.saveEvent(eventName, eventDescription, // 調用 ViewModel 的方法保存事件
                        startYear, startMonth, startDay, startHour, startMinute, endYear, endMonth, endDay, endHour, endMinute);
                getActivity().setResult(getActivity().RESULT_OK); // 返回結果給上一個界面
                getActivity().finish(); // 結束當前界面
            }
        });

        return root; // 返回根視圖
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) { // 當 Activity 創建時調用
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(CalenderThingViewModel.class); // 獲取 ViewModel 對象
    }
}
