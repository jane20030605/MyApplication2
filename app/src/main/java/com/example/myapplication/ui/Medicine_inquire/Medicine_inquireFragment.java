package com.example.myapplication.ui.Medicine_inquire;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Cache;
import okhttp3.OkHttpClient;

public class Medicine_inquireFragment extends Fragment {

    private RecyclerView recyclerView;
    private MedicineInquireAdapter adapter;
    private List<MedicineInquire> medicineList;
    private Picasso picasso;

    public Medicine_inquireFragment() {
        // 必須的空公共構造函數
    }

    private static final String MEDICINE_INQUIRE_URL = "http://100.96.1.3/api_query_medication.php";
    private static final String IMAGE_BASE_URL = "http://100.96.1.3/medimage/"; // 使用你的本地服务器URL

    public static Medicine_inquireFragment newInstance() {
        return new Medicine_inquireFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_medicine_inquire, container, false);

        // 初始化 RecyclerView
        recyclerView = view.findViewById(R.id.medicine_list);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        // 初始化空的藥品列表
        medicineList = new ArrayList<>();

        // 配置Picasso快取
        OkHttpClient client = new OkHttpClient.Builder()
                .cache(new Cache(Objects.requireNonNull(getContext()).getCacheDir(), 10 * 1024 * 1024)) // 10 MB快取
                .build();

        Picasso.Builder builder = new Picasso.Builder(getContext());
        builder.downloader(new OkHttp3Downloader(client));
        Picasso builtPicasso = builder.build();

        // 使用builder創建的Picasso實例
        adapter = new MedicineInquireAdapter(medicineList);
        recyclerView.setAdapter(adapter);

        // 檢查參數中是否包含查詢結果
        if (getArguments() != null) {
            String searchResults = getArguments().getString("searchResults");
            if (searchResults != null && !searchResults.isEmpty()) {
                try {
                    JSONArray dataArray = new JSONArray(searchResults);
                    Log.d("Medicine_inquireFragment", "收到搜尋結果: " + searchResults);
                    parseMedicineData(dataArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("Medicine_inquireFragment", "JSON 解析錯誤: " + e.getMessage());
                }
            }
        }

        return view;
    }

    private void parseMedicineData(JSONArray dataArray) {
        try {
            medicineList.clear(); // 清空現有列表
            Log.d("Medicine_inquireFragment", "解析藥品數據");

            // 解析 JSON 數據並填充到列表中
            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject medication = dataArray.getJSONObject(i);
                int medId = medication.getInt("med_id"); // 獲取藥物ID
                String imageUrl = ""; // 設置默認圖片 URL，如果沒有則為空
                if (medication.has("image_url")) {
                    imageUrl = medication.getString("image_url");
                }
                MedicineInquire medicine = new MedicineInquire(
                        medId,
                        medication.getString("drug_name"),
                        IMAGE_BASE_URL + medication.getString("atccode") + ".JPG", // 設置圖片URL
                        medication.getString("atccode"),
                        medication.getString("manufacturer"),
                        medication.getString("indications"),
                        medication.getString("shape"),
                        medication.getString("color"),
                        medication.getString("mark"),
                        medication.getString("nick"),
                        medication.getString("strip")
                );
                medicineList.add(medicine);
                Log.d("Medicine_inquireFragment", "添加藥品: " + medicine.getName());
            }

            adapter = new MedicineInquireAdapter(medicineList);
            recyclerView.setAdapter(adapter);
            Log.d("Medicine_inquireFragment", "設置適配器並填充藥品列表");

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("Medicine_inquireFragment", "JSON 解析數據時出錯: " + e.getMessage());
        }
    }

    // 靜態內部類，表示一個藥品項目
    static class MedicineInquire {
        private final int medId;
        private final String name;
        private final String imageUrl;
        private final String atccode;
        private final String manufacturer;
        private final String indications;
        private final String shape;
        private final String color;
        private final String mark;
        private final String nick;
        private final String strip;

        public MedicineInquire(int medId, String name, String imageUrl, String atccode, String manufacturer,
                               String indications, String shape, String color, String mark, String nick, String strip) {
            this.medId = medId;
            this.name = name;
            this.imageUrl = imageUrl;
            this.atccode = atccode;
            this.manufacturer = manufacturer;
            this.indications = indications;
            this.shape = shape;
            this.color = color;
            this.mark = mark;
            this.nick = nick;
            this.strip = strip;
        }

        public int getMedId() {
            return medId;
        }

        public String getName() {
            return name;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public String getAtccode() {
            return atccode;
        }

        public String getManufacturer() {
            return manufacturer;
        }

        public String getIndications() {
            return indications;
        }

        public String getShape() {
            return shape;
        }

        public String getColor() {
            return color;
        }

        public String getMark() {
            return mark;
        }

        public String getNick() {
            return nick;
        }

        public String getStrip() {
            return strip;
        }
    }

    // RecyclerView適配器類
    class MedicineInquireAdapter extends RecyclerView.Adapter<MedicineInquireAdapter.ViewHolder> {
        private final List<MedicineInquire> medicineList;

        public MedicineInquireAdapter(List<MedicineInquire> medicineList) {
            this.medicineList = medicineList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // 創建並返回 ViewHolder
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_medicine, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            // 綁定數據到 ViewHolder
            MedicineInquire medicine = medicineList.get(position);
            holder.bind(medicine, picasso);
        }

        @Override
        public int getItemCount() {
            // 返回列表項目數量
            return medicineList.size();
        }

        // ViewHolder類，持有RecyclerView項目的視圖
        class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private final TextView nameTextView;
            private final ImageView imageView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                // 初始化 ViewHolder
                nameTextView = itemView.findViewById(R.id.medicine_name);
                imageView = itemView.findViewById(R.id.medicine_image);
                itemView.setOnClickListener(this);
            }

            public void bind(MedicineInquire medicine, Picasso picasso) {
                // 將數據綁定到視圖
                nameTextView.setText(medicine.getName());
                Picasso.get()
                        .load(medicine.getImageUrl())
                        .resize(200, 200) // 調整為適合你UI的尺寸
                        .centerCrop()
                        .placeholder(R.drawable.loding) // 添加佔位圖
                        .error(R.drawable.error) // 添加錯誤圖像
                        .into(imageView);
            }

            @Override
            public void onClick(View v) {
                // 點擊列表項目時顯示詳細信息
                int position = getAdapterPosition();
                MedicineInquire medicine = medicineList.get(position);
                showMedicineDetails(medicine);
            }
        }

        private void showMedicineDetails(MedicineInquire medicine) {
            // 加載自定義布局文件
            View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.medicine_detail_dialog, null);

            // 找到彈跳視窗中的元素
            Spinner timeSpinner = dialogView.findViewById(R.id.timeSpinner);
            EditText dosageEditText = dialogView.findViewById(R.id.dosageEditText);

            // 顯示藥物詳細信息的對話框
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage(
                            "藥物名稱: " + "\n" +  medicine.getName() + "\n" +
                                    "許可證字號: " + "\n" +  medicine.getAtccode() + "\n" +
                                    "製造商: " + "\n" + medicine.getManufacturer() + "\n" +
                                    "適應症: " + "\n" + medicine.getIndications() + "\n" +
                                    "形狀: " + " " + medicine.getShape() + "\n" +
                                    "顏色: " + " " + medicine.getColor() + "\n" +
                                    "標記: " + " " + medicine.getMark()
                    )
                    .setView(dialogView)
                    .setPositiveButton("加入藥物庫", (dialog, which) -> {
                        String time = timeSpinner.getSelectedItem().toString();
                        String dosage = dosageEditText.getText().toString().trim();
                        addToMedicineCabinet(medicine, time, dosage);
                        dialog.dismiss();
                    })
                    .setNegativeButton("取消", (dialog, which) -> dialog.dismiss())
                    .show();
        }
    }

    private void addToMedicineCabinet(MedicineInquire medicine, String time, String dosage) {
        new Thread(() -> {
            try {
                SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
                String account = sharedPreferences.getString("ACCOUNT", "");

                if (account.isEmpty()) {
                    Log.e("Medicine_inquireFragment", "帳戶資訊為空");
                    return;
                }

                JSONObject postData = new JSONObject();
                postData.put("atccode", medicine.getAtccode());
                postData.put("drugName", medicine.getName());
                postData.put("manufacturer", medicine.getManufacturer());
                postData.put("medTime", time);
                postData.put("medQuantity", dosage);
                postData.put("account", account);

                Log.d("Medicine_inquireFragment", "準備發送 POST 請求，資料: " + postData.toString());

                URL url = new URL("http://100.96.1.3/api_add_to_my_medication.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                conn.setConnectTimeout(15000); // 連接超時為 15 秒
                conn.setReadTimeout(15000); // 讀取超時為 15 秒
                conn.setDoOutput(true);

                try (OutputStream os = conn.getOutputStream()) {
                    os.write(postData.toString().getBytes("UTF-8"));
                    os.flush();
                }

                int responseCode = conn.getResponseCode();
                Log.d("Medicine_inquireFragment", "POST 響應碼: " + responseCode);

                InputStream inputStream;
                if (responseCode >= 200 && responseCode < 400) {
                    inputStream = conn.getInputStream();
                } else {
                    inputStream = conn.getErrorStream();
                }

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    Log.d("Medicine_inquireFragment", "POST 響應: " + response.toString());

                    // 通知 Medicine_boxFragment 更新數據
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("data_updated", true);
                    editor.apply();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("Medicine_inquireFragment", "讀取響應時出錯: " + e.getMessage());
                }

                conn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("Medicine_inquireFragment", "發送 POST 請求時出錯: " + e.getMessage());
            }
        }).start();
    }

}
