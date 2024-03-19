package com.example.myapplication.ui.Calender;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentCalenderBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class CalenderFragment extends Fragment {

    private FragmentCalenderBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        CalenderViewModel calenderViewModel =
                new ViewModelProvider(this).get(CalenderViewModel.class);

        binding = FragmentCalenderBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // 取得DatePicker和TextView的參考
        DatePicker datePicker = binding.datePicker;
        TextView eventTextView = binding.eventTextView;

        // 在這裡可以添加日期選擇監聽器，以顯示相應的事件或處理日期相關的邏輯

        // 加號按鈕的點擊事件
        FloatingActionButton fabAddEvent = binding.fabAddEvent;
        fabAddEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 在這裡添加加號按鈕的點擊事件處理邏輯
                // 範例中只是顯示一個Toast消息
                Toast.makeText(requireContext(), "加號按鈕被點擊了", Toast.LENGTH_SHORT).show();
                // 跳轉至行事曆事件頁面
                Navigation.findNavController(v).navigate(R.id.nav_calender_thing);
            }
        });

        // 這裡是TextView的示例，您可以根據需要對其進行操作，例如設置文本內容或添加事件等
        eventTextView.setText("這裡是事件列表");

        // 將布局的根視圖返回
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}