package com.example.myapplication.ui.Calender;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private List<String> eventList;

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        public TextView tvEventName;
        public Button btnEditEvent;
        public Button btnDeleteEvent;

        public EventViewHolder(View itemView) {
            super(itemView);
            tvEventName = itemView.findViewById(R.id.tvEventName);
            btnEditEvent = itemView.findViewById(R.id.btnEditEvent);
            btnDeleteEvent = itemView.findViewById(R.id.btnDeleteEvent);
        }
    }

    public EventAdapter(List<String> eventList) {
        this.eventList = eventList;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        String selectedEventId = eventList.get(position);

        // 使用 API 客户端从 API 中获取事件的详细信息
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // 使用 API 客户端获取事件的详细信息
                    String eventDetail = CalendarApiClient.getCalendar(selectedEventId);

                    // 在 UI 线程更新 UI 控件
                    holder.itemView.post(new Runnable() {
                        @Override
                        public void run() {
                            // 将事件详细信息设置到 tvEventName 中
                            holder.tvEventName.setText(eventDetail);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    // 处理异常
                }
            }
        }).start();

        holder.btnEditEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedEventId = eventList.get(holder.getAdapterPosition());
                // 使用API客户端从API中获取事件的详细信息
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            // 使用API客户端获取事件的详细信息
                            String eventDetail = CalendarApiClient.getCalendar(selectedEventId);
                            // 将事件详细信息传递给编辑界面
                            Bundle bundle = new Bundle();
                            bundle.putString("eventDetail", eventDetail);
                            // 将事件的唯一ID也传递给编辑界面，以便进行编辑操作
                            bundle.putString("eventId", selectedEventId);
                            Navigation.findNavController(holder.itemView).navigate(R.id.nav_calender_thing, bundle);
                        } catch (Exception e) {
                            e.printStackTrace();
                            // 处理异常
                        }
                    }
                }).start();
            }
        });

        holder.btnDeleteEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                String selectedEventId = eventList.get(position);
                // 调用API来删除事件
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            // 调用API客户端删除事件
                            CalendarApiClient.deleteEvent(selectedEventId);
                            // 如果删除成功，从RecyclerView中移除该事件
                            removeEvent(position);
                        } catch (Exception e) {
                            e.printStackTrace();
                            // 处理异常
                        }
                    }
                }).start();
            }
        });
    }



    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public void addEvent(String event) {
        eventList.add(event);
        notifyItemInserted(eventList.size() - 1);
    }

    public void editEvent(int position, String newEvent) {
        eventList.set(position, newEvent);
        notifyItemChanged(position);
    }

    public void removeEvent(int position) {
        eventList.remove(position);
        notifyItemRemoved(position);
    }
}
