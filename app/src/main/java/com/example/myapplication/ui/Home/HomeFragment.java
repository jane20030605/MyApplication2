package com.example.myapplication.ui.Home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.myapplication.databinding.FragmentHomeBinding;
import com.example.myapplication.ui.Medicine.MedicineFragment;
import com.example.myapplication.ui.Medicine_box.Medicine_boxFragment;
import com.example.myapplication.ui.Memory.MemoryFragment;
import com.example.myapplication.ui.User.UserFragment;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        CalendarView calendarView = binding.calendarView;
        // 在這裡可以對 CalendarView 做任何初始化或設定

        //布局設計
        final Button radioButton7 = binding.radioButton7;
        final Button radioButton8 = binding.radioButton8;
        final Button radioButton9 = binding.radioButton9;
        final Button radioButton10 = binding.radioButton10;

        // 在這裡可以設置按鈕的監聽器，並執行相應的操作
        // 設置按鈕點擊事件
        radioButton7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent main = new Intent(getActivity(), MedicineFragment.class);
                startActivity(main);
            }
        });

        radioButton8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent main = new Intent(getActivity(), MemoryFragment.class);
                startActivity(main);
            }
        });

        radioButton9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent main = new Intent(getActivity(), UserFragment.class);
                startActivity(main);
            }
        });

        radioButton10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent main = new Intent(getActivity(), Medicine_boxFragment.class);
                startActivity(main);
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
