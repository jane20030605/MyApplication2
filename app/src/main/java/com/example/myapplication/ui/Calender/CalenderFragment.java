package com.example.myapplication.ui.Calender;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class CalenderFragment extends Fragment implements CalenderThingFragment.OnEventSavedListener {

    private List<String> eventList;
    private RecyclerView recyclerView;
    private EventAdapter eventAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_calender, container, false);

        RecyclerView recyclerView = root.findViewById(R.id.recyclerView);
        eventList = new ArrayList<>();
        eventAdapter = new EventAdapter(eventList);
        recyclerView.setAdapter(eventAdapter);

        FloatingActionButton fabAddCircularButton = root.findViewById(R.id.fabAddEvent);
        fabAddCircularButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.nav_calender_thing);
            }
        });

        fetchCalendarEvents();

        return root;
    }

    // 在 fetchCalendarEvents() 方法中
    private void fetchCalendarEvents() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String calendarData = CalendarApiClient.getCalendar("account");
                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            displayEvents(calendarData);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), "無法獲取日曆資料", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }


    @SuppressLint("StaticFieldLeak")
    private class FetchCalendarTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                // 模擬從 API 獲取日曆事件
                // 此處應該包含實際的 API 請求代碼
                return "[\"Event 1\",\"Event 2\",\"Event 3\"]";
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                displayEvents(result);
            } else {
                Toast.makeText(getContext(), "無法獲取日曆資料", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void displayEvents(String calendarData) {
        try {
            JSONArray eventsArray = new JSONArray(calendarData);
            eventList.clear(); // 清空已有的事件列表
            for (int i = 0; i < eventsArray.length(); i++) {
                String event = eventsArray.getString(i);
                eventList.add(event);
            }
            eventAdapter.notifyDataSetChanged(); // 通知 RecyclerView 更新
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // 實現 OnEventSavedListener 接口的方法
    @Override
    public void onEventSaved(String event) {
        // 添加新事件到事件列表
        addEvent(event);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void addEvent(String event) {
        eventList.add(event);
        eventAdapter.notifyDataSetChanged();
    }

    private void removeEvent(int position) {
        String deletedEvent = eventList.get(position);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String response = CalendarApiClient.deleteEvent(deletedEvent);
                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), "事件已刪除", Toast.LENGTH_SHORT).show();
                            eventList.remove(position);
                            eventAdapter.notifyDataSetChanged();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), "無法刪除事件", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }

}
