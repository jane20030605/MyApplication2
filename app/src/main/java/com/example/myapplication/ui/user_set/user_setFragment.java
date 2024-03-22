package com.example.myapplication.ui.user_set;

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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentUserSetBinding;

public class user_setFragment extends Fragment {

    private FragmentUserSetBinding binding;

    public static user_setFragment newInstance() {
        return new user_setFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //return inflater.inflate(R.layout.fragment_user_set, container, false);
        binding = FragmentUserSetBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        final TextView Text_function_set = binding.TextFunctionSet;
        final TextView Text_message_reminder = binding.TextMessageReminder;
        final TextView Text_theme_color = binding.TextThemeColor;
        final TextView Text_system_language = binding.TextSystemLanguage;
        final TextView Text_day_and_night_model = binding.TextDayAndNightModel;
        // final Switch Switch_notification = binding.SwitchNotification;
        //final Spinner Spinner_themeColor =binding.SpinnerThemeColor;
        //final Spinner Spinner_system_language = binding.SpinnerSystemLanguage;
        final RadioButton RadioButton_dayMode = binding.RadioButtonDayMode;
        final RadioButton RadioButton_nightMode = binding.RadioButtonNightMode;
        final Button Button_feedback = binding.ButtonFeedback;
        final Button Button_guidedTour = binding.ButtonGuidedTour;

        // 用戶回饋按鈕的點擊事件
        Button_feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 在這裡添加用戶回饋邏輯
                // 範例中只是顯示一個Toast消息
                Toast.makeText(requireContext(), "進行意見反饋", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(v).navigate(R.id.nav_feedback);
            }
        });

        // 使用說明按鈕的點擊事件
        Button_guidedTour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 在這裡添加使用說明邏輯
                // 範例中只是顯示一個Toast消息
                Toast.makeText(requireContext(), "新手上路，使用手冊", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(v).navigate(R.id.nav_guidedTour);
            }
        });

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        UserSetViewModel mViewModel = new ViewModelProvider(this).get(UserSetViewModel.class);
        // TODO: Use the ViewModel
    }

}