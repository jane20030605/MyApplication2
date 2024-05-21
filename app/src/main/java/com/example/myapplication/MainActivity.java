package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 綁定佈局
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        // 初始化會話管理器
        sessionManager = new SessionManager(this);

        // 設置導航視圖
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_medicine_box, R.id.nav_medicine, R.id.nav_user,
                R.id.nav_calender, R.id.nav_login, R.id.nav_memory)
                .setOpenableLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // 更新選單項目
        updateMenuItems(navigationView.getMenu());
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
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.nav_user_set);
            return true;
        } else if (id == R.id.nav_mail_for_developer) {
            sendFeedbackEmail();
            return true;
        } else if (id == R.id.nav_login) { // 添加登出菜單選項的處理邏輯
            sessionManager.login(); // 調用登入方法
            Toast.makeText(this, "已登入", Toast.LENGTH_SHORT).show();
            return true;
        }else if (id == R.id.nav_logout) { // 添加登出菜單選項的處理邏輯
            sessionManager.logout(); // 調用登出方法
            Toast.makeText(this, "已登出", Toast.LENGTH_SHORT).show();
            return true;
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

    // 更新選單項目
    private void updateMenuItems(Menu menu) {
        if (sessionManager != null) {
            MenuItem loginMenuItem = menu.findItem(R.id.nav_login);
            MenuItem logoutMenuItem = menu.findItem(R.id.nav_logout);

            if (sessionManager.isLoggedIn()) {
                loginMenuItem.setVisible(true);
                logoutMenuItem.setVisible(false);
            } else {
                loginMenuItem.setVisible(false);
                logoutMenuItem.setVisible(true);
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
