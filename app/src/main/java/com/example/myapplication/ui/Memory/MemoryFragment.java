package com.example.myapplication.ui.Memory;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentMemoryBinding;

public class MemoryFragment extends Fragment {

    private FragmentMemoryBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMemoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 設置按鈕點擊事件
        binding.buttonOpenVideo.setOnClickListener(v -> {
            // 使用 Navigation 進行跳轉
            Navigation.findNavController(v).navigate(R.id.nav_video);
        });

        // 設置 ImageView 點擊事件
        ImageView imageView = view.findViewById(R.id.imageView2);
        imageView.setOnClickListener(v -> {
            // 顯示彈跳視窗
            new AlertDialog.Builder(requireContext())
                    .setTitle("跌倒資訊")
                    .setMessage("攝影機名稱: 爸爸的房間\n" +
                            "跌倒時間: 2024-06-09 11:39:15")
                    .setPositiveButton("確定", null)
                    .show();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
