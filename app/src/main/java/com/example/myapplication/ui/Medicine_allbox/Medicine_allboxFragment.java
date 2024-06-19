package com.example.myapplication.ui.Medicine_allbox;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Medicine_allboxFragment extends Fragment {

    private RecyclerView recyclerView;
    private MedicineAllboxAdapter adapter;
    private List<MedicineAllbox> medicineList;

    private static final String MEDICINE_ALLBOX_URL = "http://100.96.1.3/api_medication.php";
    private static final String IMAGE_BASE_URL = "http://100.96.1.3/medimage/"; // 使用你的本地服务器URL

    public static Medicine_allboxFragment newInstance() {
        return new Medicine_allboxFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_medicine_allbox, container, false);

        // 初始化 RecyclerView
        recyclerView = view.findViewById(R.id.medicine_list);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        // 初始化空的藥品列表
        medicineList = new ArrayList<>();

        // 調用方法從網絡獲取藥品數據
        fetchMedicineData();

        return view;
    }

    private void fetchMedicineData() {
        // 在後台線程中執行網絡請求
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // 創建URL對象
                    URL url = new URL(MEDICINE_ALLBOX_URL);

                    // 打開連接
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                    // 設置請求方法為GET
                    connection.setRequestMethod("GET");

                    // 設置請求屬性
                    connection.setRequestProperty("Content-Type", "application/json; utf-8");
                    connection.setRequestProperty("Accept", "application/json");

                    // 獲取響應代碼
                    int responseCode = connection.getResponseCode();

                    if (responseCode == HttpURLConnection.HTTP_OK) { // 成功回應
                        // 讀取響應
                        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        String inputLine;
                        StringBuilder content = new StringBuilder();

                        while ((inputLine = in.readLine()) != null) {
                            content.append(inputLine);
                        }

                        // 關閉BufferedReader
                        in.close();

                        // 將響應內容轉換為JSON數組
                        JSONArray jsonResponse = new JSONArray(content.toString());

                        // 遍歷JSON數組並添加到藥品列表中
                        for (int i = 0; i < jsonResponse.length(); i++) {
                            JSONObject medication = jsonResponse.getJSONObject(i);
                            int medId = medication.getInt("med_id"); // 獲取藥物ID
                            MedicineAllbox medicine = new MedicineAllbox(
                                    medId,
                                    medication.getString("drug_name"),
                                    IMAGE_BASE_URL + medication.getString("atccode") + ".JPG", // 動態設置圖片URL
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
                        }

                        // 在UI線程上更新RecyclerView
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // 初始化並設置 Adapter
                                adapter = new MedicineAllboxAdapter(medicineList);
                                recyclerView.setAdapter(adapter);
                            }
                        });

                    } else { // 錯誤回應
                        Log.e("Medicine_allboxFragment", "GET request failed. Response Code: " + responseCode);
                    }

                    // 斷開連接
                    connection.disconnect();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    // 靜態內部類，表示一個藥品項目
    static class MedicineAllbox {
        private final int medId; // 添加藥物 ID
        private final String name;
        private final String imageUrl; // 修改为URL
        private final String atccode;
        private final String manufacturer;
        private final String indications;
        private final String shape;
        private final String color;
        private final String mark;
        private final String nick;
        private final String strip;

        public MedicineAllbox(int medId, String name, String imageUrl, String atccode, String manufacturer, String indications,
                              String shape, String color, String mark, String nick, String strip) {
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
    class MedicineAllboxAdapter extends RecyclerView.Adapter<MedicineAllboxAdapter.ViewHolder> {
        private final List<MedicineAllbox> medicineList;

        public MedicineAllboxAdapter(List<MedicineAllbox> medicineList) {
            this.medicineList = medicineList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_medicine, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            MedicineAllbox medicine = medicineList.get(position);
            holder.bind(medicine);
        }

        @Override
        public int getItemCount() {
            return medicineList.size();
        }

        // ViewHolder類，持有RecyclerView項目的視圖
        class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private final TextView nameTextView;
            private final ImageView imageView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                nameTextView = itemView.findViewById(R.id.medicine_name);
                imageView = itemView.findViewById(R.id.medicine_image);
                itemView.setOnClickListener(this);
            }

            public void bind(MedicineAllbox medicine) {
                nameTextView.setText(medicine.getName());
                Picasso.get().load(medicine.getImageUrl()).into(imageView); // 使用Picasso動態加載圖片
            }

            @Override
            public void onClick(View v) {
                int position = getAdapterPosition();
                MedicineAllbox medicine = medicineList.get(position);
                showMedicineDetails(medicine);
            }
        }

        private void showMedicineDetails(MedicineAllbox medicine) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(medicine.getName());
            builder.setMessage("藥品名稱: " + medicine.getName() + "\n" +
                    "ATC代碼: " + medicine.getAtccode() + "\n" +
                    "製造商: " + medicine.getManufacturer() + "\n" +
                    "適應症: " + medicine.getIndications() + "\n" +
                    "形狀: " + medicine.getShape() + "\n" +
                    "顏色: " + medicine.getColor() + "\n" +
                    "標記: " + medicine.getMark() + "\n" +
                    "是否有別名: " + medicine.getNick() + "\n" +
                    "是否在同一包裝: " + medicine.getStrip());
            builder.setPositiveButton("確定", null);
            builder.show();
        }
    }
}
