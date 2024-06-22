package com.example.myapplication.ui.Memory;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import java.util.Objects;

public class MemoryFragment extends Fragment {
    private RecyclerView recyclerView;
    private MemoryAdapter adapter;
    private List<Memory> memoryList;
    private SwipeRefreshLayout swipeRefreshLayout;

    private static final String MEMORY_URL = "http://100.96.1.3/api_get_fall.php";
    private static final String IMAGE_BASE_URL = "http://100.96.1.3/fallimage/"; // 需要修改成你的圖片路徑

    public static MemoryFragment newInstance() {
        return new MemoryFragment();
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_memory, container, false);

        // 初始化 SwipeRefreshLayout
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout_memory);
        swipeRefreshLayout.setOnRefreshListener(this::loadMemoryData);

        // 初始化 RecyclerView
        recyclerView = view.findViewById(R.id.down_list);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        // 初始化空的記憶數據列表
        memoryList = new ArrayList<>();

        Button openVideoButton = view.findViewById(R.id.button_open_video);
        openVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MemoryFragment", "打开视频按钮点击事件触发");
                try {
                    Navigation.findNavController(v).navigate(R.id.nav_video);
                } catch (Exception e) {
                    Log.e("MemoryFragment", "导航到视频界面时出现异常: " + e.getMessage());
                }
            }
        });

        // 加載記憶數據
        loadMemoryData();

        return view;
    }

    private void loadMemoryData() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        String account = sharedPreferences.getString("ACCOUNT", "");

        if (account.isEmpty()) {
            Log.e("MemoryFragment", "帳戶資訊為空");
            return;
        }

        Log.d("MemoryFragment", "開始發送請求，帳戶: " + account);

        swipeRefreshLayout.post(() -> {
            swipeRefreshLayout.setRefreshing(true); // 在主線程中設置刷新狀態
        });

        new Thread(new Runnable() {
            @SuppressLint("UseRequireInsteadOfGet")
            @Override
            public void run() {
                try {
                    // 創建URL對象
                    URL url = new URL(MEMORY_URL + "?account=" + account);

                    // 打開連接
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                    // 設置請求方法為GET
                    connection.setRequestMethod("GET");

                    // 設置請求屬性
                    connection.setRequestProperty("Content-Type", "application/json; utf-8");
                    connection.setRequestProperty("Accept", "application/json");

                    // 獲取響應碼
                    int responseCode = connection.getResponseCode();

                    if (responseCode == HttpURLConnection.HTTP_OK) { // 成功響應
                        // 讀取響應
                        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        String inputLine;
                        StringBuilder response = new StringBuilder();

                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }

                        // 關閉BufferedReader
                        in.close();

                        // 嘗試解析響應為JSON對象
                        JSONArray jsonArray = new JSONArray(response.toString());
                        parseMemoryData(jsonArray);

                    } else { // 錯誤響應
                        Log.e("MemoryFragment", "GET 請求失敗. 響應碼: " + responseCode);
                    }

                    // 斷開連接
                    connection.disconnect();

                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                    Log.e("MemoryFragment", "網路請求或JSON解析錯誤: " + e.getMessage());
                } finally {
                    Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                        swipeRefreshLayout.setRefreshing(false); // 在主線程中停止刷新動畫
                    });
                }
            }
        }).start();
    }

    @SuppressLint({"NotifyDataSetChanged", "UseRequireInsteadOfGet"})
    private void parseMemoryData(JSONArray dataArray) {
        Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
            try {
                // 清空現有列表
                memoryList.clear();

                // 遍歷JSON數組，解析每個記憶數據
                for (int i = 0; i < dataArray.length(); i++) {
                    JSONObject memoryItem = dataArray.getJSONObject(i);
                    Log.d("MemoryFragment", "解析第 " + i + " 條記憶數據: " + memoryItem.toString());

                    String recordId = memoryItem.getString("record_id");
                    String ipcamName = memoryItem.getString("ipcam_name");
                    String fallDate = memoryItem.getString("fall_date");
                    String userId = memoryItem.getString("userid");
                    String picture = memoryItem.getString("picture");
                    String imageUrl = IMAGE_BASE_URL + picture ;

                    // 創建新的記憶對象並添加到列表中
                    Memory newMemory = new Memory(recordId, ipcamName, fallDate, userId, imageUrl, picture);
                    memoryList.add(newMemory);
                }

                Log.d("MemoryFragment", "共解析到 " + memoryList.size() + " 條記憶數據");

                // 更新 RecyclerView 適配器
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

    public static class Memory {
        private final String recordId;
        private final String ipcamName;
        private final String fallDate;
        private final String userId;
        private final String imageUrl;
        private final String picture;

        public Memory(String recordId, String ipcamName, String fallDate, String userId, String imageUrl, String picture) {
            this.recordId = recordId;
            this.ipcamName = ipcamName;
            this.fallDate = fallDate;
            this.userId = userId;
            this.imageUrl = imageUrl;
            this.picture = picture;
        }

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
    }

    class MemoryAdapter extends RecyclerView.Adapter<MemoryAdapter.ViewHolder> {
        private final List<Memory> memoryList;

        public MemoryAdapter(List<Memory> memoryList) {
            this.memoryList = memoryList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_memory, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Memory memory = memoryList.get(position);
            holder.bind(memory);
        }

        @Override
        public int getItemCount() {
            return memoryList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private final ImageView imageView;
            private final TextView textView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.memory_image);
                textView = itemView.findViewById(R.id.memory_name);
                itemView.setOnClickListener(this);
            }

            public void bind(Memory memory) {
                // 使用 Picasso 加载图片
                Picasso.get()
                        .load(memory.getImageUrl())
                        .into(imageView, new Callback() {
                            @Override
                            public void onSuccess() {
                                Log.d("MemoryFragment", "Picasso 加载图片成功: " + memory.getImageUrl());
                            }

                            @Override
                            public void onError(Exception e) {
                                Log.e("MemoryFragment", "Picasso 加载图片失败: " + memory.getImageUrl() + ", " + e.getMessage());
                            }
                        });
                textView.setText(memory.getIpcamName());
            }

            @Override
            public void onClick(View v) {
                int position = getAdapterPosition();
                Memory memory = memoryList.get(position);
                showMemoryDetailDialog(memory);
            }
        }
    }

    private void showMemoryDetailDialog(Memory memory) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("跌倒相片資訊");

        // 構建跌倒資訊文本
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("攝影機位置: ").append(memory.getIpcamName()).append("\n");
        messageBuilder.append("跌倒時間: " + "\n").append(memory.getFallDate()).append("\n");

        // 設置彈窗消息內容
        builder.setMessage(messageBuilder.toString());

        builder.setPositiveButton("確定", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showErrorDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("錯誤");
        builder.setMessage(message);
        builder.setPositiveButton("確定", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
