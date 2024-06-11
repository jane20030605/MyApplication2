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
import com.example.myapplication.utils.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Set;

public class CalenderThingFragment extends Fragment {

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

        // 初始化偏好設置和會話管理器
        sharedPreferences = requireContext().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        sessionManager = new SessionManager(requireContext());

        // 設置陪伴者下拉清單
        setupCompanionsSpinner();

        // 點擊保存按鈕的動作
        binding.saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    saveEvent();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
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

    @SuppressLint("MutatingSharedPrefs")
    private void saveEvent() throws JSONException {
        // 獲取事件的相關詳細信息
        String eventName = binding.editTextThing.getText().toString();
        String eventDescription = binding.editTextEventDescription.getText().toString();
        String startDate = binding.editTextStartDate.getText().toString();
        String endDate = binding.editTextEndDate.getText().toString();
        String startTime = binding.editTextStartTime.getText().toString();
        String endTime = binding.editTextEndTime.getText().toString();
        String companions = binding.spinnerCompanions.getSelectedItem().toString();

        // 从 SharedPreferences 中读取账户信息
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        String account = sharedPreferences.getString("ACCOUNT", "");

        // 创建 CalendarEvent 对象并添加账户信息
        CalendarEvent calendarEvent = new CalendarEvent(
                eventName, eventDescription,
                startDate, endDate,
                startTime, endTime, companions, account);

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
                        Toast.makeText(requireContext(), "事件保存失败：" + message, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
    // 將 CalendarEvent 對象轉換為 JSON 字串
    private String createEventDataJson(CalendarEvent calendarEvent) throws JSONException {
        JSONObject eventDataJson = new JSONObject();
        eventDataJson.put("thing", calendarEvent.getEventName());
        eventDataJson.put("date_up", calendarEvent.getStartDate() + calendarEvent.getStartTime());
        eventDataJson.put("date_end", calendarEvent.getEndDate()  + calendarEvent.getEndTime());
        eventDataJson.put("people", calendarEvent.getCompanions());
        eventDataJson.put("describe", calendarEvent.getEventDescription());
        eventDataJson.put("account", calendarEvent.getAccount());
        return eventDataJson.toString();
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
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
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
                        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                        Calendar selectedTime = Calendar.getInstance();
                        selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        selectedTime.set(Calendar.MINUTE, minute);
                        // 將選擇的時間設置到指定的 EditText 中
                        editText.setText(timeFormat.format(selectedTime.getTime()));
                    }
                }, hourOfDay, minute, true);
        timePickerDialog.show();
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

    // 處理 API 回應
    private void handleApiResponse(String response) {
        try {
            JSONObject jsonResponse = new JSONObject(response);
            String message = jsonResponse.getString("message");
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            // 保存成功後，通知事件已保存
            if (getActivity() instanceof OnEventSavedListener) {
                ((OnEventSavedListener) getActivity()).onEventSaved(message);
                Toast.makeText(requireContext(), "保存成功，已上傳資料，請下拉頁面刷新", Toast.LENGTH_SHORT).show();
            }
            Navigation.findNavController(requireView()).navigateUp();
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "無法處理 API 回應", Toast.LENGTH_SHORT).show();
        }
    }

    public interface OnEventSavedListener {
        void onEventSaved(String event);
    }
}
