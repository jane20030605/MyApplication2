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

    private CalenderThingViewModel mViewModel; // 声明 ViewModel 对象
    private FragmentCalenderThingBinding binding; // 声明 Binding 对象

    // 创建视图时调用
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // 使用 Binding 对象将布局与 Fragment 绑定
        binding = FragmentCalenderThingBinding.inflate(inflater, container, false);
        View root = binding.getRoot(); // 获取根视图
        // 初始化界面元素
        final EditText editTextthing = binding.editTextthing;
        final EditText editTextEventDescription = binding.editTextEventDescription;
        final DatePicker datePickerStartDate = binding.datePickerStartDate;
        final DatePicker datePickerEndDate = binding.datePickerEndDate;
        final TimePicker timePickerStartTime = binding.timePickerStartTime;
        final TimePicker timePickerEndTime = binding.timePickerEndTime;
        final Button saveButton = binding.saveButton;
        final Button editButton = binding.editButton;


        // 保存按钮点击事件处理
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 获取用户输入的事件信息
                String eventName = editTextthing.getText().toString();
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
                // 调用 ViewModel 的方法保存事件
                mViewModel.saveEvent(eventName, eventDescription,
                        startYear, startMonth, startDay, startHour, startMinute, endYear, endMonth, endDay, endHour, endMinute);
            }
        });

        // 编辑按钮点击事件处理
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 获取用户输入的事件信息
                String eventName = editTextthing.getText().toString();
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
                // 调用 ViewModel 的方法编辑事件
                mViewModel.editEvent(eventName, eventDescription,
                        startYear, startMonth, startDay, startHour, startMinute, endYear, endMonth, endDay, endHour, endMinute);
            }
        });

        return root; // 返回根视图
    }

    // 当 Activity 创建时调用
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(CalenderThingViewModel.class); // 获取 ViewModel 对象
    }
}
