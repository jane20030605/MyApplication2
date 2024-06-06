package com.example.myapplication.ui.Calender;

import android.os.Bundle;
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
    private List<JSONObject> eventList;

    // 事件適配器的ViewHolder類別
    public static class EventViewHolder extends RecyclerView.ViewHolder {
        public TextView tvEventName; // 事件名稱文字視圖
        public Button btnEditEvent; // 編輯事件按鈕
        public Button btnDeleteEvent; // 刪除事件按鈕

        public EventViewHolder(View itemView) {
            super(itemView);
            tvEventName = itemView.findViewById(R.id.tvEventName); // 初始化事件名稱文字視圖
            btnEditEvent = itemView.findViewById(R.id.btnEditEvent); // 初始化編輯事件按鈕
            btnDeleteEvent = itemView.findViewById(R.id.btnDeleteEvent); // 初始化刪除事件按鈕
        }
    }

    // 建構函式，接受事件清單參數
    public EventAdapter(List<JSONObject> eventList) {
        this.eventList = eventList; // 初始化事件清單
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 創建並回傳新的事件ViewHolder
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        JSONObject event = eventList.get(position); // 取得選定事件
        String eventId = event.optString("event_id"); // 取得事件ID

        holder.tvEventName.setText(event.optString("thing")); // 設置事件名稱

        holder.btnEditEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("eventDetail", event.toString()); // 將事件詳細資訊放入Bundle
                bundle.putString("eventId", eventId); // 將事件ID放入Bundle
                Navigation.findNavController(holder.itemView).navigate(R.id.nav_calender_thing, bundle); // 導航至編輯事件頁面
            }
        });

        holder.btnDeleteEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition(); // 取得ViewHolder的位置

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String deleteApiUrl = "http://100.96.1.3/api_delete_calendar.php";
                            NetworkRequestManager.getInstance(v.getContext()).makePostRequest(deleteApiUrl, "event_id=" + eventId, new NetworkRequestManager.RequestListener() {
                                @Override
                                public void onSuccess(String response) {
                                    removeEvent(position); // 從清單中移除事件
                                }

                                @Override
                                public void onError(String error) {
                                    // 顯示錯誤消息
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
