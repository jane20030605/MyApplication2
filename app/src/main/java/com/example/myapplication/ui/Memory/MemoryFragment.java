package com.example.myapplication.ui.Memory;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.myapplication.Main_SQL;
import com.example.myapplication.databinding.FragmentMemoryBinding;

public class MemoryFragment extends Fragment {

    private FragmentMemoryBinding binding;
    private Main_SQL mainSql;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMemoryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textMemory;

        // 初始化 Main_SQL 實例
        mainSql = new Main_SQL(requireContext());

        // 從資料庫中獲取資料並設置到 TextView 中
        String dataFromDatabase = mainSql.getDataFromDatabase().toString();
        textView.setText(dataFromDatabase);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
