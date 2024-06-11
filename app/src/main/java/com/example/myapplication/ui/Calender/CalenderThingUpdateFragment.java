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

import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentCalenderThingBinding;
import com.example.myapplication.ui.Calender.apiclient.CalendarAddClient;
import com.example.myapplication.utils.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Set;

public class CalenderThingUpdateFragment extends Fragment {

    private FragmentCalenderThingBinding binding;
    private CalendarAddClient apiClient;
    private SharedPreferences sharedPreferences;
    private SessionManager sessionManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiClient = new CalendarAddClient();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCalenderThingBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // 初始化偏好设置和会话管理器
        sharedPreferences = requireContext().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        sessionManager = new SessionManager(requireContext());

        // 设置陪伴者下拉列表
        setupCompanionsSpinner();

        // 点击保存按钮的动作
        binding.saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    saveEvent();
                    Navigation.findNavController(v).navigate(R.id.nav_calender);
                    Toast.makeText(requireContext(), "更新成功，已上傳資料\n請下拉頁面刷新", Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        // 点击取消按钮的动作
        binding.noSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(requireView()).navigateUp();
            }
        });

        // 点击开始日期文本框的动作
        binding.editTextStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(binding.editTextStartDate);
            }
        });

        // 点击结束日期文本框的动作
        binding.editTextEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(binding.editTextEndDate);
            }
        });

        // 点击开始时间文本框的动作
        binding.editTextStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(binding.editTextStartTime);
            }
        });

        // 点击结束时间文本框的动作
        binding.editTextEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(binding.editTextEndTime);
            }
        });

        return root;
    }

    // 设置陪伴者下拉列表
    private void setupCompanionsSpinner() {
        // 定义默认的陪伴者列表
        String[] companionsList = {"家人", "朋友", "個人", "工作"};
        // 从共享偏好中获取陪伴者列表
        SharedPreferences sharedPreferences =
                requireActivity().getSharedPreferences("MyCalendar", Context.MODE_PRIVATE);
        Set<String> companionsSet =
                sharedPreferences.getStringSet("companionsList", null);

        // 如果共享偏好中存在陪伴者列表，则使用共享偏好中的列表
        if (companionsSet != null) {
            companionsList = companionsSet.toArray(new String[0]);
        }

        // 创建陪伴者下拉列表的适配器并设置给 Spinner
        ArrayAdapter<String> companionsAdapter =
                new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_item, companionsList);
        binding.spinnerCompanions.setAdapter(companionsAdapter);
    }

    @SuppressLint("MutatingSharedPrefs")
    private void saveEvent() throws JSONException {
        // 获取事件的相关详细信息
        String eventName = binding.editTextThing.getText().toString();
        String eventDescription = binding.editTextEventDescription.getText().toString();
        String startDate = binding.editTextStartDate.getText().toString();
        String endDate = binding.editTextEndDate.getText().toString();
        String startTime = binding.editTextStartTime.getText().toString();
        String endTime = binding.editTextEndTime.getText().toString();
        String companions = binding.spinnerCompanions.getSelectedItem().toString();

        // 从 SharedPreferences 中读取账户信息
        SharedPreferences sharedPreferences =
                requireActivity().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        String account = sharedPreferences.getString("ACCOUNT", "");

        // 创建 CalendarEvent 对象
        CalendarEvent calendarEvent = new CalendarEvent(
                eventName, eventDescription,
                startDate, endDate,startTime, endTime,
                companions, account );

        // 将 CalendarEvent 对象转换为 JSON 字符串
        String eventDataJson = createEventDataJson(calendarEvent);

        // 使用 CalendarAddClient 类中的 addEvent 方法保存事件
        apiClient.addEvent(eventDataJson, new CalendarAddClient.CalendarCallback() {
            @Override
            public void onSuccess(String message) {
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        handleApiResponse("{\"message\": \"" + message + "\"}");
                    }
                });
            }

            @Override
            public void onError(String message) {
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(requireContext(), "事件更新失败：" + message, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    // 将 CalendarEvent 对象转换为 JSON 字符串
    private String createEventDataJson(CalendarEvent calendarEvent) throws JSONException {
        JSONObject eventDataJson = new JSONObject();
        eventDataJson.put("event_id", calendarEvent.getEventName());
        eventDataJson.put("thing", calendarEvent.getEventName());
        eventDataJson.put("date_up", calendarEvent.getStartDate() + " " + calendarEvent.getStartTime());
        eventDataJson.put("date_end", calendarEvent.getEndDate() + " " + calendarEvent.getEndTime());
        eventDataJson.put("people", calendarEvent.getCompanions());
        eventDataJson.put("describe", calendarEvent.getEventDescription());
        eventDataJson.put("account", calendarEvent.getAccount());
        return eventDataJson.toString();
    }

    // 显示日期选择对话框
    private void showDatePickerDialog(final EditText editText) {

        // 获取当前日期
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        // 创建日期选择对话框
        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                // 设置日期选择监听器
                new DatePickerDialog.OnDateSetListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        Calendar selectedDate = Calendar.getInstance();
                        selectedDate.set(year, month, dayOfMonth);
                        // 将选择的日期设置到指定的 EditText 中
                        editText.setText(dateFormat.format(selectedDate.getTime()));
                    }
                }, year, month, dayOfMonth);
        datePickerDialog.show();
    }

    // 显示时间选择对话框
    private void showTimePickerDialog(final EditText editText) {
        // 获取当前时间
        Calendar calendar = Calendar.getInstance();
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // 创建时间选择对话框
        TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(),
                // 设置时间选择监听器
                new TimePickerDialog.OnTimeSetListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                        Calendar selectedTime = Calendar.getInstance();
                        selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        selectedTime.set(Calendar.MINUTE, minute);
                        // 将选择的时间设置到指定的 EditText 中
                        editText.setText(timeFormat.format(selectedTime.getTime()));
                    }
                }, hourOfDay, minute, true);
        timePickerDialog.show();
    }

    // 处理 API 响应
    private void handleApiResponse(String response) {
        try {
            JSONObject jsonResponse = new JSONObject(response);
            String message = jsonResponse.getString("message");
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            if (message.equals("更新成功")) {
                // 如果更新成功，执行相应操作，比如返回上一页或者刷新数据等
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // 保存成功后，通知事件已保存
                        if (getActivity() instanceof OnEventSavedListener) {
                            ((OnEventSavedListener) getActivity()).onEventSaved(message);
                        }
                        // 导航回上一页或者其他操作
                        Navigation.findNavController(requireView()).navigateUp();
                    }
                });
            } else {
                // 如果更新失败，显示错误信息
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(requireContext(), "事件更新失败：" + message, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
            requireActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(requireContext(), "无法处理 API 响应", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public interface OnEventSavedListener {
        void onEventSaved(String event);
    }
}