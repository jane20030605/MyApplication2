package com.example.myapplication.utils;

import com.example.myapplication.models.User;
import java.util.HashMap;
import java.util.Map;

public class UserManager {
    private static final UserManager instance = new UserManager();
    private Map<String, User> users;

    private UserManager() {
        // 初始化用户数据
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

    // 可以添加其他管理用户数据的方法，如添加用户、删除用户等
}