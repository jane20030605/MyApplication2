// 匯入Intent類別

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.databinding.FragmentCalenderBinding;
import com.example.myapplication.ui.Calender.CalenderViewModel;
import com.example.myapplication.ui.calender_thing.calender_thingFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CalenderFragment extends Fragment {

    private FragmentCalenderBinding binding;
    private TextView eventTextView;

    // 創建視圖時調用
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // 使用資料繫結將布局與片段綁定
        binding = FragmentCalenderBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // 取得事件文本視圖的參考
        eventTextView = binding.eventTextView;

        // 處理加號按鈕的點擊事件
        FloatingActionButton fabAddEvent = binding.fabAddEvent;
        fabAddEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳轉至行事曆事件頁面
                Intent intent = new Intent(getActivity(), calender_thingFragment.class);
                startActivityForResult(intent, 1);
            }
        });

        return root;
    }

    // 將事件信息添加到事件列表中的方法
    private void addEventToEventList(String eventName, String eventDescription, Calendar eventDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String eventDateString = dateFormat.format(eventDate.getTime());
        String eventText = eventTextView.getText().toString();
        eventText += "\n" + "事件名稱：" + eventName + "\n" + "描述：" + eventDescription + "\n" + "日期：" + eventDateString + "\n";
        eventTextView.setText(eventText);
    }

    // 接收從行事曆事件頁面返回的結果
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == getActivity().RESULT_OK) {
                // 更新事件列表
                updateEventList();
            }
        }
    }

    // 更新事件列表的方法
    private void updateEventList() {
        // 從ViewModel中獲取事件列表數據
        // 假設此處使用ViewModel獲取數據，你可以根據實際情況修改
        CalenderViewModel viewModel = new ViewModelProvider(this).get(CalenderViewModel.class);
        String eventName = viewModel.getEventName();
        String eventDescription = viewModel.getEventDescription();
        Calendar eventDate = viewModel.getEventDate();

        // 將事件信息添加到事件列表中
        addEventToEventList(eventName, eventDescription, eventDate);
    }

    // 在視圖銷毀時執行
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
