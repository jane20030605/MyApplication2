package com.example.myapplication;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordHasher {

    public static String hashPassword(String password) {
        try {
            // 创建MessageDigest对象
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            // 将密码转换为字节数组
            byte[] bytes = password.getBytes();
            // 执行哈希计算
            byte[] hashedBytes = digest.digest(bytes);
            // 将字节数组转换为十六进制字符串
            StringBuilder builder = new StringBuilder();
            for (byte b : hashedBytes) {
                builder.append(String.format("%02x", b));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}

