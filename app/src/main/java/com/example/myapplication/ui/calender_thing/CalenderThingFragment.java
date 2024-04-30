package com.example.myapplication.ui.calender_thing;

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

            String eventName = getArguments().getString("eventName");
            String eventDescription = getArguments().getString("eventDescription");
            String sdate = getArguments().getString("sdate");
            String edate = getArguments().getString("edate");
            String stime = getArguments().getString("stime");
            String etime = getArguments().getString("etime");
            String companions = getArguments().getString("companions");

            binding.editTextThing.setText(eventName);
            binding.editTextEventDescription.setText(eventDescription);
            binding.editTextStartDate.setText(sdate);
            binding.editTextEndDate.setText(edate);
            binding.editTextStartTime.setText(stime);
            binding.editTextEndTime.setText(etime);

            ArrayAdapter<String> adapter = (ArrayAdapter<String>) binding.spinnerCompanions.getAdapter();
            int position = adapter.getPosition(companions);
            binding.spinnerCompanions.setSelection(position);
        }

        return root;
    }

    // 設置陪伴者下拉清單
    private void setupCompanionsSpinner() {
        String[] companionsList = {"家人", "朋友", "個人", "工作"};
        SharedPreferences sharedPreferences =
                requireActivity().getSharedPreferences("MyCalendar", Context.MODE_PRIVATE);
        Set<String> companionsSet =
                sharedPreferences.getStringSet("companionsList", null);

        if (companionsSet != null) {
            companionsList = companionsSet.toArray(new String[0]);
        }

        ArrayAdapter<String> companionsAdapter =
                new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_item, companionsList);
        binding.spinnerCompanions.setAdapter(companionsAdapter);
    }

    // 保存事件
    @SuppressLint("MutatingSharedPrefs")
    private void saveEvent() {
        String eventDetails = getEventDetailsFromInputs();

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyCalendar", Context.MODE_PRIVATE);
        Set<String> eventsSet = sharedPreferences.getStringSet("events", new HashSet<>());
        eventsSet.add(eventDetails);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet("events", eventsSet);
        editor.apply();

        Toast.makeText(requireContext(), "事件已保存", Toast.LENGTH_SHORT).show();

        Navigation.findNavController(requireView()).navigateUp();
    }

    // 從輸入框中獲取事件細節
    private String getEventDetailsFromInputs() {
        String eventName = binding.editTextThing.getText().toString();
        String eventDescription = binding.editTextEventDescription.getText().toString();
        String sdate = binding.editTextStartDate.getText().toString();
        String edate = binding.editTextEndDate.getText().toString();
        String stime = binding.editTextStartTime.getText().toString();
        String etime = binding.editTextEndTime.getText().toString();
        String companions = binding.spinnerCompanions.getSelectedItem().toString();

        String eventDetailsBuilder =
                "事件名稱: " + eventName + "\n" +
                        "事件描述: " + eventDescription + "\n" +
                        "起始日期: " + sdate + "\n" +
                        "結束日期: " + edate + "\n" +
                        "起始時間: " + stime + "\n" +
                        "結束時間: " + etime + "\n" +
                        "事件對象: " + companions;

        return eventDetailsBuilder;
    }

    // 顯示日期選擇對話框
    private void showDatePickerDialog(final EditText editText) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
                        Calendar selectedDate = Calendar.getInstance();
                        selectedDate.set(year, month, dayOfMonth);
                        editText.setText(dateFormat.format(selectedDate.getTime()));
                    }
                }, year, month, dayOfMonth);
        datePickerDialog.show();
    }

    // 顯示時間選擇對話框
    private void showTimePickerDialog(final EditText editText) {
        Calendar calendar = Calendar.getInstance();
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(),
                new TimePickerDialog.OnTimeSetListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                        Calendar selectedTime = Calendar.getInstance();
                        selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        selectedTime.set(Calendar.MINUTE, minute);
                        editText.setText(timeFormat.format(selectedTime.getTime()));
                    }
                }, hourOfDay, minute, true);
        timePickerDialog.show();
    }
}
