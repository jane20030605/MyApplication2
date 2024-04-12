package com.example.myapplication.utils;

import com.example.myapplication.models.User;
import java.util.HashMap;
import java.util.Map;

public class UserManager {
    private static final UserManager instance = new UserManager();
    private Map<String, User> users;

    private UserManager() {
        // 初始化用戶數據
        users = new HashMap<>();
        users.put("LYJ", new User("LYJ", "LYJane"));
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