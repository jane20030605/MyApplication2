package com.example.myapplication.ui.Calender;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.myapplication.databinding.FragmentCalenderThingBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class CalenderThingFragment extends Fragment {

    private FragmentCalenderThingBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCalenderThingBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // 設置陪伴者下拉清單
        setupCompanionsSpinner();

        // 點擊保存按鈕的動作
        binding.saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveEvent();
            }
        });

        // 點擊取消按鈕的動作
        binding.noSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(requireView()).navigateUp();
            }
        });

        // 點擊開始日期文本框的動作
        binding.editTextStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(binding.editTextStartDate);
            }
        });

        // 點擊結束日期文本框的動作
        binding.editTextEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(binding.editTextEndDate);
            }
        });

        // 點擊開始時間文本框的動作
        binding.editTextStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(binding.editTextStartTime);
            }
        });

        // 點擊結束時間文本框的動作
        binding.editTextEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(binding.editTextEndTime);
            }
        });

        // 如果有編輯參數，則填充編輯事件的表單
        if (getArguments() != null && getArguments().containsKey("isEditing")
                && getArguments().getBoolean("isEditing")) {

            String eventDetails = getArguments().getString("eventDetails");
            populateEventDetails(eventDetails);
        }

        return root;
    }

    // 設置陪伴者下拉清單
    private void setupCompanionsSpinner() {
        // 定義預設的陪伴者清單
        String[] companionsList = {"家人", "朋友", "個人", "工作"};
        // 從共享偏好中獲取陪伴者清單
        SharedPreferences sharedPreferences =
                requireActivity().getSharedPreferences("MyCalendar", Context.MODE_PRIVATE);
        Set<String> companionsSet =
                sharedPreferences.getStringSet("companionsList", null);

        // 如果共享偏好中存在陪伴者清單，則使用共享偏好中的清單
        if (companionsSet != null) {
            companionsList = companionsSet.toArray(new String[0]);
        }

        // 建立陪伴者下拉清單的適配器並設置給 Spinner
        ArrayAdapter<String> companionsAdapter =
                new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_item, companionsList);
        binding.spinnerCompanions.setAdapter(companionsAdapter);
    }

    // 保存事件
    @SuppressLint("MutatingSharedPrefs")
    private void saveEvent() {
        // 獲取事件的相關詳細信息
        String eventName = binding.editTextThing.getText().toString();
        String eventDescription = binding.editTextEventDescription.getText().toString();
        String startDate = binding.editTextStartDate.getText().toString();
        String endDate = binding.editTextEndDate.getText().toString();
        String startTime = binding.editTextStartTime.getText().toString();
        String endTime = binding.editTextEndTime.getText().toString();
        String companions = binding.spinnerCompanions.getSelectedItem().toString();

        // 將事件詳細信息組合成一個字串
        String eventDetails = eventName + "\n" + eventDescription + "\n" +
                startDate + "\n" + endDate + "\n" +
                startTime + "\n" + endTime + "\n" +
                companions;

        // 從共享偏好中獲取事件集合
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyCalendar", Context.MODE_PRIVATE);
        Set<String> eventsSet = sharedPreferences.getStringSet("events", new HashSet<>());
        // 將新的事件詳細信息添加到事件集合中
        eventsSet.add(eventDetails);

        // 將更新後的事件集合保存回共享偏好中
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet("events", eventsSet);
        editor.apply();

        // 顯示保存成功的提示消息
        Toast.makeText(requireContext(), "事件已保存", Toast.LENGTH_SHORT).show();

        // 返回上一個 Fragment
        Navigation.findNavController(requireView()).navigateUp();
    }

    // 填充事件詳細信息
    private void populateEventDetails(String eventDetails) {
        // 將事件詳細信息拆分成各個部分
        String[] details = eventDetails.split("\n");
        if (details.length == 7) {
            // 將各個部分的信息填充到相應的 EditText 中
            binding.editTextThing.setText(details[0]);
            binding.editTextEventDescription.setText(details[1]);
            binding.editTextStartDate.setText(details[2]);
            binding.editTextEndDate.setText(details[3]);
            binding.editTextStartTime.setText(details[4]);
            binding.editTextEndTime.setText(details[5]);

            // 從 Spinner 的適配器中獲取陪伴者在清單中的位置並設置給 Spinner
            ArrayAdapter<String> adapter = (ArrayAdapter<String>) binding.spinnerCompanions.getAdapter();
            int position = adapter.getPosition(details[6]);
            binding.spinnerCompanions.setSelection(position);
        }
    }

    // 顯示日期選擇對話框
    private void showDatePickerDialog(final EditText editText) {
        // 獲取當前日期
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        // 創建日期選擇對話框
        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                // 設置日期選擇監聽器
                new DatePickerDialog.OnDateSetListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
                        Calendar selectedDate = Calendar.getInstance();
                        selectedDate.set(year, month, dayOfMonth);
                        // 將選擇的日期設置到指定的 EditText 中
                        editText.setText(dateFormat.format(selectedDate.getTime()));
                    }
                }, year, month, dayOfMonth);
        datePickerDialog.show();
    }

    // 顯示時間選擇對話框
    private void showTimePickerDialog(final EditText editText) {
        // 獲取當前時間
        Calendar calendar = Calendar.getInstance();
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // 創建時間選擇對話框
        TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(),
                // 設置時間選擇監聽器
                new TimePickerDialog.OnTimeSetListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                        Calendar selectedTime = Calendar.getInstance();
                        selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        selectedTime.set(Calendar.MINUTE, minute);
                        // 將選擇的時間設置到指定的 EditText 中
                        editText.setText(timeFormat.format(selectedTime.getTime()));
                    }
                }, hourOfDay, minute, true);
        timePickerDialog.show();
    }
}