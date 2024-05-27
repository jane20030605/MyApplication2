package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.myapplication.databinding.ActivityMainBinding;
import com.example.myapplication.utils.SessionManager;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private SessionManager sessionManager;
    private Menu menu;

    private String username;
    private String userEmail;

    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 綁定佈局
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 設置工具欄
        setSupportActionBar(binding.appBarMain.toolbar);

        // 抽屜佈局
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        // 初始化會話管理器
        sessionManager = new SessionManager(this);

        // 設置導航視圖的配置
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_medicine_box, R.id.nav_medicine, R.id.nav_user,
                R.id.nav_calender, R.id.nav_login, R.id.nav_memory)
                .setOpenableLayout(drawer)
                .build();

        // 獲取NavController並設置導航
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // 獲取菜單並更新菜單項目
        this.menu = navigationView.getMenu();
        updateMenuItems();

        // 獲取從登入頁面傳遞過來的用戶名稱，如果為空則使用默認用戶名稱
        username = getIntent().getStringExtra("USERNAME");
        if (username == null) {
            SharedPreferences sharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
            username = sharedPreferences.getString("USERNAME", "User");
        }

        // 獲取從註冊介面傳遞過來的郵箱，如果為空則空白
        userEmail = getIntent().getStringExtra("EMAIL");
        if (userEmail == null) {
            // 從SharedPreferences中獲取郵箱
            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            userEmail = sharedPreferences.getString("EMAIL", "Android@example.gmail.com");
        }

        // 更新導航頭部的用戶名稱和郵箱
        updateNavHeader(username, userEmail);
    }

    // 更新導航頭部的用戶名稱和郵箱
    private void updateNavHeader(String username, String userEmail) {
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = headerView.findViewById(R.id.test_user);
        TextView navEmail = headerView.findViewById(R.id.text_mail);
        navUsername.setText(username);
        navEmail.setText(userEmail);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_setting) {
            // 導航到設置頁面
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.nav_user_set);
            return true;

        } else if (id == R.id.nav_mail_for_developer) {
            // 發送反饋郵件
            sendFeedbackEmail();
            return true;

        } else if (id == R.id.nav_login) {
            // 處理登入邏輯
            sessionManager.login();
            Toast.makeText(this, "登入成功", Toast.LENGTH_SHORT).show();
            // 更新菜單項目
            updateMenuItems();
            // 記住用戶登錄狀態
            rememberUser();

        } else if (id == R.id.nav_logout) {
            // 處理登出邏輯
            sessionManager.logout();
            Toast.makeText(this, "登出成功", Toast.LENGTH_SHORT).show();
            // 導航到登入頁面
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.nav_login);
        }

        return super.onOptionsItemSelected(item);
    }

    // 發送反饋郵件
    private void sendFeedbackEmail() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", "yijanelin2@gmail.com", null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "用戶意見反饋");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "");
        startActivity(Intent.createChooser(emailIntent, "選擇郵件客戶端"));
    }

    // 更新菜單項目的可見性
    public void updateMenuItems() {
        if (sessionManager != null && menu != null) {
            MenuItem loginMenuItem = menu.findItem(R.id.nav_login);
            MenuItem logoutMenuItem = menu.findItem(R.id.nav_logout);

            if (sessionManager.isLoggedIn()) {
                loginMenuItem.setVisible(false);
                logoutMenuItem.setVisible(true);
            } else {
                loginMenuItem.setVisible(true);
                logoutMenuItem.setVisible(false);
            }
        }
    }

    // 記住用戶登錄狀態
    private void rememberUser() {
        SharedPreferences sharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isRemembered", true);
        editor.putString("USERNAME", username);
        editor.putString("USER_EMAIL", userEmail);
        editor.apply();
    }

    // 圖片選擇功能
    public void selectImageFromGallery(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            ImageView imageView = findViewById(R.id.imageView);
            imageView.setImageURI(uri);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
