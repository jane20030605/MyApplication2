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

import com.example.myapplication.network.ExampleEntity;
import com.example.myapplication.databinding.ActivityMainBinding;
import com.example.myapplication.network.ApiService;
import com.example.myapplication.utils.SessionManager;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private SessionManager sessionManager;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        sessionManager = new SessionManager(this);

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_medicine_box, R.id.nav_medicine, R.id.nav_user,
                R.id.nav_calender, R.id.nav_login, R.id.nav_memory)
                .setOpenableLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // 初始化 Retrofit
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://26.110.164.151/Untitled-1.php") // 替換為你的後端 API 地址
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        apiService = retrofit.create(ApiService.class);

        // 調用 fetchDataFromAPI 方法來從 API 獲取資料
        fetchDataFromAPI();

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
        }

        return super.onOptionsItemSelected(item);
    }

    private void sendFeedbackEmail() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", "yijanelin2@gmail.com", null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "用戶意見反饋");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "");
        startActivity(Intent.createChooser(emailIntent, "選擇郵件客戶端"));
    }

    private void updateMenuItems(Menu menu) {
        if (sessionManager != null) {
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

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    // 在這裡定義呼叫 API 的方法
    private void fetchDataFromAPI() {
        apiService.getData().enqueue(new Callback<List<ExampleEntity>>() {
            @Override
            public void onResponse(@NonNull Call<List<ExampleEntity>> call, @NonNull Response<List<ExampleEntity>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ExampleEntity> data = response.body();
                    // 處理從 API 返回的資料
                } else {
                    Toast.makeText(MainActivity.this, "無法獲取資料", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ExampleEntity>> call, @NonNull Throwable t) {
                Toast.makeText(MainActivity.this, "網路錯誤：" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
