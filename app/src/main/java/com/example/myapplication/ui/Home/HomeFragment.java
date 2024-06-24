package com.example.myapplication.ui.Home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentHomeBinding;
import com.example.myapplication.utils.SessionManager;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private SessionManager sessionManager;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // 初始化 SessionManager
        sessionManager = new SessionManager(requireContext());

        CalendarView calendarView = binding.calendarView;
        // 在這裡可以對 CalendarView 做任何初始化或設定

        // 布局設計
        final Button Button_medicine = binding.ButtonMedicine;
        final Button Button_memory = binding.ButtonMemory;
        final Button Button_userdata = binding.ButtonUserdata;
        final Button Button_medicine_box = binding.ButtonMedicineBox;

        // 設置按鈕點擊事件
        Button_medicine_box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sessionManager.isLoggedIn()) {
                    Navigation.findNavController(view).navigate(R.id.nav_medicine_box);
                } else {
                    Navigation.findNavController(view).navigate(R.id.nav_login);
                }
            }
        });

        Button_memory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sessionManager.isLoggedIn()) {
                    Navigation.findNavController(view).navigate(R.id.nav_memory);
                } else {
                    Navigation.findNavController(view).navigate(R.id.nav_login);
                }
            }
        });

        Button_userdata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sessionManager.isLoggedIn()) {
                    Navigation.findNavController(view).navigate(R.id.nav_user_data);
                } else {
                    Navigation.findNavController(view).navigate(R.id.nav_login);
                }
            }
        });

        Button_medicine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.nav_medicine);
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
