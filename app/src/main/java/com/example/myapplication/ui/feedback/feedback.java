package com.example.myapplication.ui.feedback;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication.databinding.FragmentFeedbackBinding;
import com.example.myapplication.R;

public class feedback extends Fragment {

    public static feedback newInstance() {
        return new feedback();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 使用 Data Binding 加載布局文件
        FragmentFeedbackBinding binding = FragmentFeedbackBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();

        // 設置提交按鈕點擊事件
        binding.buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 獲取用戶輸入的意見文本
                String feedbackText = binding.editTextFeedback.getText().toString();

                // 發送意見文本到指定郵箱
                sendFeedbackEmail(feedbackText);
            }
        });

        // 設置取消按鈕點擊事件
        binding.buttonNotSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 返回上一個介面
                requireActivity().onBackPressed();
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // 初始化 ViewModel
        FeedbackViewModel mViewModel = new ViewModelProvider(this).get(FeedbackViewModel.class);
        // TODO: 使用 ViewModel
    }

    // 發送意見文本到指定郵箱
    private void sendFeedbackEmail(String feedbackText) {
        // 創建發送郵件的意圖
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", "yijanelin2@gmail.com", null));
        // 設置郵件主題
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "用戶意見反饋");
        // 設置郵件內容，包括用户输入的意见文本
        emailIntent.putExtra(Intent.EXTRA_TEXT, feedbackText);
        // 啟動郵件客戶端
        startActivity(Intent.createChooser(emailIntent, "選擇郵件客戶端"));
    }
}
