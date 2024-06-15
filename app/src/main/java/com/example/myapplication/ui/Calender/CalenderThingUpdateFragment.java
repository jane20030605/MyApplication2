package com.example.myapplication.ui.Calender;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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
import com.example.myapplication.ui.Calender.apiclient.CalendarUpdateClient;
import com.example.myapplication.utils.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

public class CalenderThingUpdateFragment extends Fragment {

    private FragmentCalenderThingBinding binding;
    private CalendarUpdateClient apiClient;
    private CalendarUpdateEvent calendarUpdateEvent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiClient = new CalendarUpdateClient();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCalenderThingBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // 初始化偏好设置和会话管理器
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        SessionManager sessionManager = new SessionManager(requireContext());

        // 设置陪伴者下拉列表
        setupCompanionsSpinner();
        // 获取传递的事件ID，如果存在则设置事件ID
        Bundle args = getArguments();

        if (args != null && args.containsKey("eventId")) {

            String date_up = Objects.requireNonNull(args.getString("date_up")).split(" ")[0];
            String start_time = Objects.requireNonNull(args.getString("date_up")).split(" ")[1];
            String date_end = Objects.requireNonNull(args.getString("date_end")).split(" ")[0];
            String end_time = Objects.requireNonNull(args.getString("date_end")).split(" ")[1];

            // 初始化 calendarUpdateEvent 对象
            calendarUpdateEvent = new CalendarUpdateEvent();
            calendarUpdateEvent.setEvent_id(args.getString("eventId"));
            calendarUpdateEvent.setAccount(args.getString("account"));
            calendarUpdateEvent.setEventDescription(args.getString("describe"));
            calendarUpdateEvent.setEventName(args.getString("thing"));
            calendarUpdateEvent.setCompanions(args.getString("people"));
            calendarUpdateEvent.setStartDate(date_up);
            calendarUpdateEvent.setStartTime(start_time);
            calendarUpdateEvent.setEndDate(date_end);
            calendarUpdateEvent.setEndTime(end_time);
            Log.e("calendarUpdateEvent", calendarUpdateEvent.getAllValue());
            // 你可能需要从服务器获取事件的详细信息并填充表单
            binding.editTextThing.setText(calendarUpdateEvent.getEventName());
            binding.editTextEventDescription.setText(calendarUpdateEvent.getEventDescription());
            binding.editTextStartDate.setText(calendarUpdateEvent.getStartDate());
            binding.editTextStartTime.setText(calendarUpdateEvent.getStartTime());
            binding.editTextEndDate.setText(calendarUpdateEvent.getEndDate());
            binding.editTextEndTime.setText(calendarUpdateEvent.getEndTime());
            ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) binding.spinnerCompanions.getAdapter();
            int position = adapter.getPosition(calendarUpdateEvent.getCompanions());
            binding.spinnerCompanions.setSelection(position);
        }

        // 点击保存按钮的动作
        binding.saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // 调用保存事件的方法
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
        // 如果 calendarUpdateEvent 为 null，直接返回
        if (calendarUpdateEvent == null) {
            return;
        }
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

        // 更新 CalendarEvent 对象的属性值
        calendarUpdateEvent.setEventName(eventName);
        calendarUpdateEvent.setEventDescription(eventDescription);
        calendarUpdateEvent.setStartDate(startDate);
        calendarUpdateEvent.setEndDate(endDate);
        calendarUpdateEvent.setStartTime(startTime);
        calendarUpdateEvent.setEndTime(endTime);
        calendarUpdateEvent.setCompanions(companions);
        calendarUpdateEvent.setAccount(account);

        // 转换 CalendarUpdateEvent 对象为 JSON 字符串
        String eventDataJson = createEventDataJson(calendarUpdateEvent);
        Log.e("eventDataJson", eventDataJson);
        // 使用 CalendarUpdateClient 类中的 updateEvent 方法保存事件
        apiClient.updateEvent(eventDataJson, new CalendarUpdateClient.CalendarCallback() {
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

    // 将 CalendarUpdateEvent 对象转换为 JSON 字符串
    private String createEventDataJson(CalendarUpdateEvent calendarUpdateEvent) throws JSONException {
        JSONObject eventDataJson = new JSONObject();
        eventDataJson.put("event_id", calendarUpdateEvent.getEvent_id());
        eventDataJson.put("thing", calendarUpdateEvent.getEventName());
        eventDataJson.put("date_up", calendarUpdateEvent.getStartDate() + " " + calendarUpdateEvent.getStartTime());
        eventDataJson.put("date_end", calendarUpdateEvent.getEndDate() + " " + calendarUpdateEvent.getEndTime());
        eventDataJson.put("people", calendarUpdateEvent.getCompanions());
        eventDataJson.put("describe", calendarUpdateEvent.getEventDescription());
        eventDataJson.put("account", calendarUpdateEvent.getAccount());
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
                        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
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
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "api無法使用", Toast.LENGTH_SHORT).show();
        }
    }
}