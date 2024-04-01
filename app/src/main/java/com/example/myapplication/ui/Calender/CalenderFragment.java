package com.example.myapplication.ui.Calender;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.WindowDecorActionBar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentCalenderBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CalenderFragment extends Fragment {

    private FragmentCalenderBinding binding;
    @SuppressLint("RestrictedApi")
    private WindowDecorActionBar.TabImpl eventTextView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        CalenderViewModel calenderViewModel =
                new ViewModelProvider(this).get(CalenderViewModel.class);

        binding = FragmentCalenderBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // 取得DatePicker和TextView的參考
        DatePicker datePicker = binding.datePicker;
        TextView eventTextView = binding.eventTextView;

        // 加號按鈕的點擊事件
        FloatingActionButton fabAddEvent = binding.fabAddEvent;
        fabAddEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳轉至行事曆事件頁面
                Navigation.findNavController(v).navigate(R.id.nav_calender_thing);
            }
        });

        // 將布局的根視圖返回
        return root;
    }

    // 將事件信息添加到事件列表中的方法
    @SuppressLint("RestrictedApi")
    private void addEventToEventList(String eventName, String eventDescription, Calendar eventDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String eventDateString = dateFormat.format(eventDate.getTime());
        @SuppressLint("RestrictedApi") String eventText = eventTextView.getText().toString();
        eventText += "\n" + "事件名稱：" + eventName + "\n" + "描述：" + eventDescription + "\n" + "日期：" + eventDateString + "\n";
        eventTextView.setText(eventText);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}