package com.example.myapplication.utils;

import com.example.myapplication.models.User;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;

public class UserManager {
    private static final UserManager instance = new UserManager();
    private Map<String, User> users; // 使用者資料映射


    private UserManager() {
        users = new HashMap<>();
        // 添加初始使用者
        users.put("LYJ", new User("LYJ", "yijanelin2@gmail","LYJane"));

    }

    public static UserManager getInstance() {
        return instance;
    }

    public User getUser(String username) {
        return users.get(username);
    }

    public void addUser(User newUser) {
        users.put(newUser.getUsername(), newUser);
    }
}

