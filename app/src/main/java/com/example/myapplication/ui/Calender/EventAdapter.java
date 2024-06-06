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
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {
    private List<String> eventList;

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
    public EventAdapter(List<String> eventList) {
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
        String selectedEventId = eventList.get(position); // 取得選定事件的ID

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String eventDetail = CalendarApiClient.getCalendar(selectedEventId); // 從API取得事件詳細資訊
                    holder.itemView.post(new Runnable() {
                        @Override
                        public void run() {
                            holder.tvEventName.setText(eventDetail); // 在UI執行緒上設定事件名稱
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        holder.btnEditEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String eventDetail = CalendarApiClient.getCalendar(selectedEventId); // 取得事件詳細資訊
                            Bundle bundle = new Bundle();
                            bundle.putString("eventDetail", eventDetail); // 將事件詳細資訊放入Bundle
                            bundle.putString("eventId", selectedEventId); // 將事件ID放入Bundle
                            Navigation.findNavController(holder.itemView).navigate(R.id.nav_calender_thing, bundle); // 導航至編輯事件頁面
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });

        holder.btnDeleteEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition(); // 取得ViewHolder的位置
                String selectedEventId = eventList.get(position); // 取得選定事件的ID

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            CalendarApiClient.deleteEvent(selectedEventId); // 刪除事件
                            removeEvent(position); // 從清單中移除事件
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
    public void addEvent(String event) {
        eventList.add(event); // 將事件新增到清單
        notifyItemInserted(eventList.size() - 1); // 通知Adapter有新的事件插入
    }

    // 編輯事件
    public void editEvent(int position, String newEvent) {
        eventList.set(position, newEvent); // 在指定位置編輯事件
        notifyItemChanged(position); // 通知Adapter有事件資料變更
    }

    // 從清單中移除事件
    public void removeEvent(int position) {
        eventList.remove(position); // 從清單中移除事件
        notifyItemRemoved(position); // 通知Adapter有事件被移除
    }
}