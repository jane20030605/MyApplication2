package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.myapplication.databinding.ActivityMainBinding;
import com.example.myapplication.ui.Calender.apiclient.CalendarGetClient;
import com.example.myapplication.utils.SessionManager;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

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
                R.id.nav_home, R.id.nav_medicine_box, R.id.nav_medicine, R.id.nav_user_data,
                R.id.nav_calender, R.id.nav_login, R.id.nav_memory, R.id.nav_logout,
                R.id.nav_user_set, R.id.nav_mail_for_developer/*,R.id.nav_video*/)
                .setOpenableLayout(drawer)
                .build();

        // 獲取NavController並設置導航
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // 設置 NavigationView 的選項選擇監聽器
        navigationView.setNavigationItemSelectedListener(this);

        // 獲取菜單並更新菜單項目
        this.menu = navigationView.getMenu();
        updateMenuItems();

        // 獲取當前登入的帳號名稱
        username = sessionManager.getCurrentLoggedInAccount();

        // 獲取從登入頁面傳遞過來的用戶名稱，如果為空則使用默認用戶名稱
        username = getIntent().getStringExtra("ACCOUNT");
        if (username == null) {
            SharedPreferences sharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
            username = sharedPreferences.getString("ACCOUNT", "User");
        }

        // 獲取從註冊介面傳遞過來的郵箱，如果為空則為預設
        userEmail = getIntent().getStringExtra("EMAIL");
        if (userEmail == null) {
            // 從SharedPreferences中獲取郵箱
            SharedPreferences sharedPreferences = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);;
            userEmail = sharedPreferences.getString("EMAIL", "Android@example.gmail.com");

        }
        // 調用獲取行事曆資料的方法
        fetchCalendarData(username);

        // 更新導航頭部的用戶名稱和郵箱
        updateNavHeader(username, userEmail, false);
    }
    // 從後端服務獲取行事曆數據


    // 更新導航頭部的用戶名稱和郵箱
    @SuppressLint("SetTextI18n")
    private void updateNavHeader(String username, String userEmail, boolean isDefault) {
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = headerView.findViewById(R.id.test_user);
        TextView navEmail = headerView.findViewById(R.id.text_mail);

        if (isDefault) {
            navUsername.setText("User");
            navEmail.setText("Android@example.gmail.com");
        } else {
            navUsername.setText(username);
            navEmail.setText(userEmail);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    private void fetchCalendarData(String ACCOUNT) {
        try {
            // 使用您的行事曆 API 客戶端獲取用戶的行事曆數據
            String calendarData = CalendarGetClient.getCalendar(username);

            // 處理從後端獲取的行事曆數據，例如顯示在 UI 上
            // 這裡僅示例獲取成功時的處理，您需要根據您的應用程序邏輯進行相應處理
            Log.d("CalendarData", calendarData);
            // 在這裡更新 UI，顯示行事曆數據
        } catch (Exception e) {
            e.printStackTrace();
            // 在這裡處理異常情況，例如顯示錯誤消息
            Log.e("CalendarData", "出現異常訊息，請稍後");
        }
    }
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);

        if (id != R.id.nav_medicine && id != R.id.nav_setting && id != R.id.nav_home && id != R.id.nav_mail_for_developer) {
            // 如果使用者尚未登入，導航到登入介面
            if (!sessionManager.isLoggedIn()) {
                navController.navigate(R.id.nav_login);
                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        }

        if (id == R.id.nav_setting) {
            // 跳到設置介面
            navController.navigate(R.id.nav_user_set);

        } else if (id == R.id.nav_mail_for_developer) {
            // 寄信給開發者
            sendFeedbackEmail();

        } else if (id == R.id.nav_home) {
            // 跳轉到主畫面
            navController.navigate(R.id.nav_home);

        } else if (id == R.id.nav_login) {
            // 處理登錄邏輯
            sessionManager.login(ACCOUNT_SERVICE);
            navController.navigate(R.id.nav_login);
            // 記住使用者登錄狀態
            rememberUser();
            // 更新選單項目
            updateMenuItems();

        } else if (id == R.id.nav_logout) {
            // 處理登出邏輯
            sessionManager.logout();
            // 清除使用者登錄狀態
            clearRememberedUser();
            // 跳轉到登錄 Fragment
            navController.navigate(R.id.nav_login);

        } else if (id == R.id.nav_medicine_box) {
            // 跳轉到個人藥物庫介面
            navController.navigate(R.id.nav_medicine_box);

        } else if (id == R.id.nav_medicine) {
            // 跳轉到藥物查詢介面
            navController.navigate(R.id.nav_medicine);

        } else if (id == R.id.nav_memory) {
            // 跳轉到跌倒紀錄介面
            navController.navigate(R.id.nav_memory);

        } else if (id == R.id.nav_calender) {
            // 跳轉到行事曆介面
            navController.navigate(R.id.nav_calender);

        } else if (id == R.id.nav_user_data) {
            // 跳轉到使用者資料介面
            navController.navigate(R.id.nav_user_data);

        }/*else if (id == R.id.nav_video) {
            // 跳轉到视频片段
            navController.navigate(R.id.nav_video);
        }*/

        // 在任何選項被點擊後關閉抽屜佈局
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    // 發送反饋郵件
    private void sendFeedbackEmail() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", "lsofeveryone@gmail.com", null));
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
        editor.putString("ACCOUNT", username);
        editor.putString("USER_EMAIL", userEmail);
        editor.apply();
    }

    // 清除用戶登錄狀態
    private void clearRememberedUser() {
        Log.d("MainActivity", "clearRememberedUser: 清除用戶登錄狀態...");
        sessionManager.logout(); // 確保登出

        // 清除用戶登錄狀態
        SharedPreferences sharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("isRemembered"); // 移除登錄狀態
        editor.remove("ACCOUNT"); // 移除用戶名稱
        editor.remove("USER_EMAIL"); // 移除用戶郵箱
        editor.apply();

        // 清除用戶信息
        SharedPreferences userPreferences = getSharedPreferences("UserInfo", MODE_PRIVATE);
        SharedPreferences.Editor userEditor = userPreferences.edit();
        userEditor.clear(); // 清除所有用戶信息
        userEditor.apply();

        // 更新導航頭部為預設值
        updateNavHeader(null, null, true);

        updateMenuItems(); // 更新菜單項目
        Log.d("MainActivity", "clearRememberedUser: 用戶登錄狀態已清除。");
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


    public void updateNavHeaderEmail(String email) {
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView navEmail = headerView.findViewById(R.id.text_mail);
        navEmail.setText(email);
    }
}