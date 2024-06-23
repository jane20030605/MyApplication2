package com.example.myapplication.ui.user_set;

import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.myapplication.AppStateReceiver;
import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentUserSetBinding;

public class user_setFragment extends Fragment {

    private FragmentUserSetBinding binding;
    private SharedPreferences sharedPreferences;

    public static user_setFragment newInstance() {
        return new user_setFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentUserSetBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // 初始化 SharedPreferences
        sharedPreferences = requireContext().getSharedPreferences("UserSettings", Context.MODE_PRIVATE);

        // 绑定控件
        final TextView Text_function_set = binding.TextFunctionSet;
        final TextView Text_day_and_night_model = binding.TextDayAndNightModel;
        final RadioButton RadioButton_dayMode = binding.RadioButtonDayMode;
        final RadioButton RadioButton_nightMode = binding.RadioButtonNightMode;
        final Button Button_feedback = binding.ButtonFeedback;
        final Button Button_guidedTour = binding.ButtonGuidedTour;

        // 设置初始的日夜模式选择
        int savedNightMode = getSavedNightMode();
        if (savedNightMode == AppCompatDelegate.MODE_NIGHT_YES) {
            RadioButton_nightMode.setChecked(true);
        } else {
            RadioButton_dayMode.setChecked(true);
        }

        // 设置RadioButton的点击事件
        RadioButton_dayMode.setOnClickListener(v -> {
            if (savedNightMode != AppCompatDelegate.MODE_NIGHT_NO) {
                saveNightModeSetting(AppCompatDelegate.MODE_NIGHT_NO);
                setDayNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });

        RadioButton_nightMode.setOnClickListener(v -> {
            if (savedNightMode != AppCompatDelegate.MODE_NIGHT_YES) {
                saveNightModeSetting(AppCompatDelegate.MODE_NIGHT_YES);
                setDayNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
        });

        // 读取当前通知状态并设置初始选择
        boolean isNotificationEnabled = sharedPreferences.getBoolean("notifications_enabled", false);

        // 用户反馈按钮的点击事件
        Button_feedback.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "進行意見反饋", Toast.LENGTH_SHORT).show();
            Navigation.findNavController(v).navigate(R.id.nav_feedback);
        });

        // 使用说明按钮的点击事件
        Button_guidedTour.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "新手上路，使用手冊", Toast.LENGTH_SHORT).show();
            Navigation.findNavController(v).navigate(R.id.nav_guidedTour);
        });

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // 初始化ViewModel
        UserSetViewModel mViewModel = new ViewModelProvider(this).get(UserSetViewModel.class);
        // TODO: Use the ViewModel
    }

    // 保存夜间模式设置到SharedPreferences
    private void saveNightModeSetting(int nightMode) {
        AppCompatDelegate.setDefaultNightMode(nightMode);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("night_mode", nightMode);
        editor.apply();
    }

    // 设置日夜模式
    private void setDayNightMode(int nightMode) {
        AppCompatDelegate.setDefaultNightMode(nightMode);
        requireActivity().recreate();
    }

    // 读取保存的夜间模式设置，默认为跟随系统
    private int getSavedNightMode() {
        return sharedPreferences.getInt("night_mode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
    }

    // 保存通知状态到SharedPreferences
    private void saveNotificationState(boolean isEnabled) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("notifications_enabled", isEnabled);
        editor.apply();
    }

    // 启用或禁用广播接收器
    private void toggleAppStateReceiver(boolean enable) {
        ComponentName receiver = new ComponentName(requireContext(), AppStateReceiver.class);
        PackageManager pm = requireContext().getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                enable ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED :
                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }
}
