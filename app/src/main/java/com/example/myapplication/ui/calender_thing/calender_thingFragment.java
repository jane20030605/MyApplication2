package com.example.myapplication.ui.calender_thing;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentCalenderThingBinding;
import com.example.myapplication.databinding.FragmentRegistrationBinding;
import com.example.myapplication.ui.registration.RegistrationViewModel;
public class calender_thingFragment extends Fragment {

    private CalenderThingViewModel mViewModel;
    private FragmentCalenderThingBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        CalenderThingViewModel calenderThingViewModel =
                new ViewModelProvider(this).get(CalenderThingViewModel.class);

        binding = FragmentCalenderThingBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final EditText editTextthing = binding.editTextthing;
        final EditText editTextEventDescription = binding.editTextEventDescription;
        final DatePicker datePickerStartDate = binding.datePickerStartDate;
        final DatePicker datePickerEndDate = binding.datePickerEndDate;
        final TimePicker timePickerStartTime = binding.timePickerStartTime;
        final TimePicker timePickerEndTime = binding.timePickerEndTime;
        final Spinner spinnerCompanions = binding.spinnerCompanions;
        final Button save_button = binding.saveButton;
        final Button editbutton = binding.editButton;

        // 保存事件按钮点击事件处理
        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String eventName = editTextthing.getText().toString();
                String eventDescription = editTextEventDescription.getText().toString();
                // 获取日期选择器选择的日期
                int startYear = datePickerStartDate.getYear();
                int startMonth = datePickerStartDate.getMonth();
                int startDay = datePickerStartDate.getDayOfMonth();
                int endYear = datePickerEndDate.getYear();
                int endMonth = datePickerEndDate.getMonth();
                int endDay = datePickerEndDate.getDayOfMonth();
                // 获取时间选择器选择的时间
                int startHour = timePickerStartTime.getCurrentHour();
                int startMinute = timePickerStartTime.getCurrentMinute();
                int endHour = timePickerEndTime.getCurrentHour();
                int endMinute = timePickerEndTime.getCurrentMinute();
                // 处理保存事件的逻辑，可以调用 ViewModel 中的方法
                mViewModel.saveEvent(eventName, eventDescription,
                        startYear, startMonth, startDay, startHour, startMinute, endYear, endMonth, endDay, endHour, endMinute);
            }
        });

        // 编辑事件按钮点击事件处理
        editbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 处理编辑事件的逻辑，可以调用 ViewModel 中的方法
            }
        });

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(CalenderThingViewModel.class);
        // TODO: Use the ViewModel
    }
}
