package com.example.myapplication.ui.Memory;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.myapplication.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MemoryFragment extends Fragment {
    private RecyclerView recyclerView;
    private MemoryAdapter adapter;
    private List<Memory> memoryList;
    private SwipeRefreshLayout swipeRefreshLayout;

    private static final String MEMORY_URL = "http://100.96.1.3/api_get_fall.php"; // 網絡請求的URL
    private static final String IMAGE_BASE_URL = "http://100.96.1.3/fallimage/"; // 圖片的基礎URL

    private static final String KEY_MEMORY_LIST = "memoryList"; // 保存在Bundle中的記憶數據列表的鍵名稱

    public static MemoryFragment newInstance() {
        return new MemoryFragment();
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_memory, container, false);

        // 初始化 SwipeRefreshLayout
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout_memory);
        swipeRefreshLayout.setOnRefreshListener(this::loadMemoryData); // 設置下拉刷新監聽器

        // 初始化 RecyclerView
        recyclerView = view.findViewById(R.id.down_list);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2)); // 使用網格布局，每行顯示兩列

        // 恢復實例狀態
        if (savedInstanceState != null) {
            memoryList = savedInstanceState.getParcelableArrayList(KEY_MEMORY_LIST); // 從保存的Bundle中恢復記憶數據列表
        } else {
            memoryList = new ArrayList<>();
            loadMemoryData(); // 如果沒有保存的狀態，則加載記憶數據
        }

        adapter = new MemoryAdapter(memoryList); // 初始化適配器
        recyclerView.setAdapter(adapter); // 設置適配器到RecyclerView中

        // 打開視頻按鈕的點擊事件監聽器
        Button openVideoButton = view.findViewById(R.id.button_open_video);
        openVideoButton.setOnClickListener(v -> {
            Log.d("MemoryFragment", "打開視頻按鈕點擊事件觸發");
            try {
                Navigation.findNavController(v).navigate(R.id.nav_video); // 導航到視頻Fragment
            } catch (Exception e) {
                Log.e("MemoryFragment", "導航到視頻界面時出現異常: " + e.getMessage());
            }
        });

        return view; // 返回Fragment的View
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(KEY_MEMORY_LIST, new ArrayList<>(memoryList)); // 將記憶數據列表保存到Bundle中
    }

    // 加載記憶數據的方法
    private void loadMemoryData() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        String account = sharedPreferences.getString("ACCOUNT", ""); // 從SharedPreferences中獲取帳戶信息

        if (account.isEmpty()) {
            Log.e("MemoryFragment", "帳戶資訊為空");
            return;
        }

        Log.d("MemoryFragment", "開始發送請求，帳戶: " + account);

        swipeRefreshLayout.post(() -> swipeRefreshLayout.setRefreshing(true)); // 在主線程中設置刷新狀態

        // 新建一個線程來執行網絡請求
        new Thread(() -> {
            try {
                URL url = new URL(MEMORY_URL + "?account=" + account); // 構建URL對象
                HttpURLConnection connection = (HttpURLConnection) url.openConnection(); // 打開HTTP連接
                connection.setRequestMethod("GET"); // 設置請求方法為GET
                connection.setRequestProperty("Content-Type", "application/json; utf-8"); // 設置請求頭的Content-Type屬性
                connection.setRequestProperty("Accept", "application/json"); // 設置請求頭的Accept屬性

                int responseCode = connection.getResponseCode(); // 獲取響應碼

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream())); // 獲取輸入流
                    StringBuilder response = new StringBuilder();
                    String inputLine;

                    // 讀取輸入流中的數據
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }

                    in.close();
                    JSONArray jsonArray = new JSONArray(response.toString()); // 將JSON字符串轉換為JSONArray對象
                    parseMemoryData(jsonArray); // 解析獲取到的記憶數據

                } else {
                    Log.e("MemoryFragment", "GET 請求失敗. 響應碼: " + responseCode);
                }

                connection.disconnect(); // 斷開連接

            } catch (IOException | JSONException e) {
                e.printStackTrace();
                Log.e("MemoryFragment", "網路請求或JSON解析錯誤: " + e.getMessage());
            } finally {
                getActivity().runOnUiThread(() -> swipeRefreshLayout.setRefreshing(false)); // 在主線程中停止刷新
            }
        }).start(); // 啟動線程
    }

    // 解析記憶數據的方法
    @SuppressLint({"NotifyDataSetChanged", "UseRequireInsteadOfGet"})
    private void parseMemoryData(JSONArray dataArray) {
        getActivity().runOnUiThread(() -> {
            try {
                memoryList.clear(); // 清空原有的記憶數據列表

                // 遍歷JSON數組
                for (int i = 0; i < dataArray.length(); i++) {
                    JSONObject memoryItem = dataArray.getJSONObject(i); // 獲取單個記憶數據對象
                    Log.d("MemoryFragment", "解析第 " + i + " 條記憶數據: " + memoryItem.toString());

                    // 解析記憶數據的各個字段
                    String recordId = memoryItem.getString("record_id");
                    String ipcamName = memoryItem.getString("ipcam_name");
                    String fallDate = memoryItem.getString("fall_date");
                    String userId = memoryItem.getString("userid");
                    String picture = memoryItem.getString("picture");
                    String imageUrl = IMAGE_BASE_URL + picture;

                    // 創建Memory對象並添加到記憶數據列表中
                    Memory newMemory = new Memory(recordId, ipcamName, fallDate, userId, imageUrl, picture);
                    memoryList.add(newMemory);
                }

                Log.d("MemoryFragment", "共解析到 " + memoryList.size() + " 條記憶數據");

                // 根據適配器是否為空來更新RecyclerView中的數據
                if (adapter == null) {
                    adapter = new MemoryAdapter(memoryList);
                    recyclerView.setAdapter(adapter);
                } else {
                    adapter.notifyDataSetChanged();
                }

            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("MemoryFragment", "JSON 解析數據時出錯: " + e.getMessage());
            }
        });
    }

    // Memory類，實現Parcelable接口
    public static class Memory implements Parcelable {
        private final String recordId; // 記錄ID
        private final String ipcamName; // 攝像頭名稱
        private final String fallDate; // 跌倒日期
        private final String userId; // 用戶ID
        private final String imageUrl; // 圖片URL
        private final String picture; // 圖片名稱

        // Memory類的構造方法
        public Memory(String recordId, String ipcamName, String fallDate, String userId, String imageUrl, String picture) {
            this.recordId = recordId;
            this.ipcamName = ipcamName;
            this.fallDate = fallDate;
            this.userId = userId;
            this.imageUrl = imageUrl;
            this.picture = picture;
        }

        // Parcelable接口的構造方法
        protected Memory(Parcel in) {
            recordId = in.readString();
            ipcamName = in.readString();
            fallDate = in.readString();
            userId = in.readString();
            imageUrl = in.readString();
            picture = in.readString();
        }

        // Parcelable接口的Creator實現
        public static final Creator<Memory> CREATOR = new Creator<Memory>() {
            @Override
            public Memory createFromParcel(Parcel in) {
                return new Memory(in);
            }

            @Override
            public Memory[] newArray(int size) {
                return new Memory[size];
            }
        };

        // Getter方法，用於獲取Memory對象的各個字段值
        public String getRecordId() {
            return recordId;
        }

        public String getIpcamName() {
            return ipcamName;
        }

        public String getFallDate() {
            return fallDate;
        }

        public String getUserId() {
            return userId;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public String getPicture() {
            return picture;
        }

        // 實現Parcelable接口的方法，描述對象特徵內容的標志位，此處無特殊需求，返回0即可
        @Override
        public int describeContents() {
            return 0;
        }

        // 實現Parcelable接口的方法，將對象序列化為一個Parcel對象
        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(recordId);
            dest.writeString(ipcamName);
            dest.writeString(fallDate);
            dest.writeString(userId);
            dest.writeString(imageUrl);
            dest.writeString(picture);
        }
    }

    // MemoryAdapter類，用於RecyclerView的數據綁定
    class MemoryAdapter extends RecyclerView.Adapter<MemoryAdapter.ViewHolder> {
        private final List<Memory> memoryList; // 記憶數據列表

        // MemoryAdapter類的構造方法
        public MemoryAdapter(List<Memory> memoryList) {
            this.memoryList = memoryList;
        }

        // 創建ViewHolder，將item_memory佈局加載進來作為每個子項的顯示
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_memory, parent, false);
            return new ViewHolder(view);
        }

        // 綁定ViewHolder，將數據與界面進行綁定
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Memory memory = memoryList.get(position);
            holder.bind(memory);
        }

        // 返回記憶數據列表的大小
        @Override
        public int getItemCount() {
            return memoryList.size();
        }

        // ViewHolder類，用於保存item_memory佈局中的控件
        class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private final ImageView imageView; // 圖片控件
            private final TextView textView; // 文本控件

            // ViewHolder類的構造方法
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.memory_image); // 初始化圖片控件
                textView = itemView.findViewById(R.id.memory_name); // 初始化文本控件
                itemView.setOnClickListener(this); // 設置點擊事件監聽器
            }

            // 將數據與界面進行綁定
            public void bind(Memory memory) {
                textView.setText(memory.getIpcamName()); // 設置文本內容
                Picasso.get().load(memory.getImageUrl()).into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        // 圖片加載成功
                    }

                    @Override
                    public void onError(Exception e) {
                        // 圖片加載失敗
                        imageView.setImageResource(R.drawable.down); // 替換為您的占位符圖像資源
                    }
                });
            }

            // 點擊事件處理
            @Override
            public void onClick(View v) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Memory memory = memoryList.get(position);
                    showDetailsDialog(memory); // 顯示詳情對話框
                }
            }

            // 顯示詳情對話框
            private void showDetailsDialog(Memory memory) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext()); // 創建AlertDialog.Builder對象
                builder.setTitle("跌倒紀錄詳情")
                        .setMessage(
                                "攝像頭名稱: " + memory.getIpcamName() + "\n" +
                                "跌倒日期: " + "\n" + memory.getFallDate())
                        .setPositiveButton("確定", (dialog, which) -> dialog.dismiss()) // 設置確定按鈕
                        .show(); // 顯示對話框
            }
        }
    }
}
