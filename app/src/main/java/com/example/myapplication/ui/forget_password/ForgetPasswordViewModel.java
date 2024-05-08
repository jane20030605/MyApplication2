package com.example.myapplication.ui.forget_password;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.net.Uri;

import androidx.lifecycle.ViewModel;

import java.util.Random;

public class ForgetPasswordViewModel extends ViewModel {

    private static final String ALLOWED_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    // 發送新密碼並返回發送狀態
    public boolean sendNewPassword(String email) {
        // 生成一個新的隨機密碼
        String newPassword = generateNewPassword(10); // 假設密碼長度為 10 個字符
        // 寄送新密碼至用戶郵箱
        boolean sent = sendEmail(email, newPassword);
        if (sent) {
            // 如果郵件成功發送，則打印新密碼和用戶的電子郵件地址
            logNewPasswordAndEmail(newPassword, email);
        }
        // 返回郵件發送狀態
        return sent;
    }

    // 生成新密碼的方法
    private String generateNewPassword(int length) {
        StringBuilder newPassword = new StringBuilder();
        Random random = new Random();
        // 隨機生成指定長度的密碼
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(ALLOWED_CHARS.length());
            newPassword.append(ALLOWED_CHARS.charAt(randomIndex));
        }
        return newPassword.toString();
    }

    // 寄送郵件的方法
    private boolean sendEmail(String email, String newPassword) {
        try {
            // 創建一個 Intent 來啟動郵件應用程序
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            // 設置郵件地址
            intent.setData(Uri.parse("mailto:" + email));
            // 設置主題
            intent.putExtra(Intent.EXTRA_SUBJECT, "你的新密碼");
            // 設置郵件內容
            intent.putExtra(Intent.EXTRA_TEXT, "新密碼為: " + newPassword);
            // 使用 startActivity 啟動郵件應用程序
            startActivity(intent);
            return true; // 返回 true 表示郵件已成功發送
        } catch (Exception e) {
            // 如果發送郵件時出現異常，返回 false
            e.printStackTrace();
            return false;
        }
    }

    private void startActivity(Intent intent) {
    }

    // 打印新密碼和用戶的電子郵件地址
    private void logNewPasswordAndEmail(String newPassword, String email) {
        android.util.Log.d("忘記密碼", "新密碼為: " + newPassword + ", 電子信箱: " + email);
    }
}
