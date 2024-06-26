package com.example.myapplication.ui.Calender;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.utils.NetworkRequestManager;

import org.json.JSONObject;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {
    private final List<JSONObject> eventList;

    // 事件適配器的 ViewHolder 類別
    public static class EventViewHolder extends RecyclerView.ViewHolder {
        public TextView tvEventName; // 事件名稱
        public TextView tvStartDate; // 開始日期
        public TextView tvEndDate;   // 結束日期
        public TextView tvCompanions; // 同伴
        public TextView tvEventthing; // 事件詳情
        public Button btnEditEvent;  // 編輯事件按鈕
        public Button btnDeleteEvent; // 刪除事件按鈕

        public EventViewHolder(View itemView) {
            super(itemView);
            tvEventName = itemView.findViewById(R.id.tvEventName);
            tvStartDate = itemView.findViewById(R.id.tvStartDate);
            tvEndDate = itemView.findViewById(R.id.tvEndDate);
            tvCompanions = itemView.findViewById(R.id.tvCompanions);
            tvEventthing = itemView.findViewById(R.id.tvEventthing);
            btnEditEvent = itemView.findViewById(R.id.btnEditEvent);
            btnDeleteEvent = itemView.findViewById(R.id.btnDeleteEvent);
        }
    }

    public EventAdapter(List<JSONObject> eventList) {
        this.eventList = eventList;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 創建並回傳新的事件 ViewHolder
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        JSONObject event = eventList.get(position);

        // 設置事件的相關資訊
        holder.tvEventName.setText(event.optString("thing")); // 事件名稱
        holder.tvStartDate.setText(event.optString("date_up")); // 開始日期
        holder.tvEndDate.setText(event.optString("date_end")); // 結束日期
        holder.tvCompanions.setText(event.optString("people")); // 同伴
        holder.tvEventthing.setText(event.optString("describe")); // 事件詳情

        String eventId = event.optString("event_id"); // 取得事件ID

        // 根据当前日夜间模式设置文本颜色
        int textColor = getTextColor(holder.itemView.getContext());
        holder.tvEventName.setTextColor(textColor);
        holder.tvStartDate.setTextColor(textColor);
        holder.tvEndDate.setTextColor(textColor);
        holder.tvCompanions.setTextColor(textColor);
        holder.tvEventthing.setTextColor(textColor);

        // 設置編輯事件按鈕的點擊監聽器
        holder.btnEditEvent.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("eventId", eventId); // 將事件ID放入 Bundle
            bundle.putString("account", event.optString("account"));
            bundle.putString("thing", event.optString("thing"));
            bundle.putString("date_up", event.optString("date_up"));
            bundle.putString("date_end", event.optString("date_end"));
            bundle.putString("describe", event.optString("describe"));
            bundle.putString("people", event.optString("people"));
            Navigation.findNavController(holder.itemView).navigate(R.id.nav_calender_thing_update, bundle); // 導航至編輯事件頁面
        });

        // 設置刪除事件按鈕的點擊監聽器
        holder.btnDeleteEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition(); // 取得 ViewHolder 的位置

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String deleteApiUrl = "http://100.96.1.3/api_delete_calendar.php";
                            // 向 API 發送 POST 請求刪除事件
                            NetworkRequestManager.getInstance(v.getContext()).makePostRequest(deleteApiUrl, "event_id=" + eventId, new NetworkRequestManager.RequestListener() {
                                @Override
                                public void onSuccess(String response) {
                                    removeEvent(position); // 從清單中移除事件
                                }

                                @Override
                                public void onError(String error) {
                                    // 確保在主線程上顯示錯誤信息
                                    new Handler(Looper.getMainLooper()).post(() -> {
                                        // 顯示錯誤消息
                                    });
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
    }

    private int getTextColor(Context context) {
        int currentNightMode = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            return context.getResources().getColor(R.color.white); // 夜间模式下的文本颜色
        } else {
            return context.getResources().getColor(R.color.textColorPrimary); // 日间模式下的文本颜色
        }
    }

    @Override
    public int getItemCount() {
        return eventList.size(); // 回傳事件清單的大小
    }

    // 新增事件到清單
    public void addEvent(JSONObject event) {
        eventList.add(event); // 將事件加入清單
        notifyItemInserted(eventList.size() - 1); // 通知適配器有新項目插入
    }

    // 從清單中移除事件
    public void removeEvent(int position) {
        eventList.remove(position); // 移除指定位置的事件
        notifyItemRemoved(position); // 通知適配器有項目移除
    }
}
