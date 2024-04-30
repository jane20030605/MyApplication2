package com.example.myapplication.ui.feedback;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.myapplication.databinding.FragmentFeedbackBinding;

public class feedback extends Fragment {

    private FragmentFeedbackBinding binding; // 使用 Data Binding 綁定佈局

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 通過 Data Binding 加載佈局文件
        binding = FragmentFeedbackBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot(); // 獲取根視圖

        // 設置提交按鈕的點擊事件
        binding.buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 獲取用戶輸入的意見文本
                String feedbackText = binding.editTextFeedback.getText().toString();
                // 發送意見文本到指定郵箱
                sendFeedbackEmail(feedbackText);
            }
        });
        // 設置取消按鈕的點擊事件
        binding.buttonNotSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 返回上一個介面
                requireActivity().onBackPressed();
            }
        });
        return rootView; // 返回根視圖
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
        // 顯示提示訊息
        showFeedbackSentMessage();
    }
    // 顯示意見送出相關訊息
    private void showFeedbackSentMessage() {
        Context context = getContext();
        if (context != null) {
            Toast.makeText(context, "請在此輸入意見\n並利用郵件發送給管理員", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // 清空 Data Binding 對象
    }
}
