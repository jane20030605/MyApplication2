package com.example.myapplication.ui.Medicine_inquire;

import android.app.AlertDialog;
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
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Medicine_inquireFragment extends Fragment {

    private RecyclerView recyclerView;
    private MedicineInquireAdapter adapter;
    private List<MedicineInquire> medicineList;

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

        // 檢查參數中是否包含查詢結果
        if (getArguments() != null) {
            String searchResults = getArguments().getString("searchResults");
            if (searchResults != null && !searchResults.isEmpty()) {
                try {
                    JSONArray dataArray = new JSONArray(searchResults);
                    Log.d("Medicine_inquireFragment", "Search results received: " + searchResults);
                    parseMedicineData(dataArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("Medicine_inquireFragment", "JSONException: " + e.getMessage());
                }
            }
        }

        return view;
    }

    private void parseMedicineData(JSONArray dataArray) {
        try {
            medicineList.clear(); // 清空現有列表
            Log.d("Medicine_inquireFragment", "Parsing medicine data");

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
                Log.d("Medicine_inquireFragment", "Added medicine: " + medicine.getName());
            }

            adapter = new MedicineInquireAdapter(medicineList);
            recyclerView.setAdapter(adapter);
            Log.d("Medicine_inquireFragment", "Adapter set with medicine list");

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("Medicine_inquireFragment", "JSONException while parsing data: " + e.getMessage());
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
            holder.bind(medicine);
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

            public void bind(MedicineInquire medicine) {
                // 將數據綁定到視圖
                nameTextView.setText(medicine.getName());
                if (!medicine.getImageUrl().isEmpty()) {
                    Picasso.get().load(medicine.getImageUrl()).into(imageView);
                } else {
                    imageView.setImageResource(R.drawable.medicine_1); // 使用佔位符圖片
                }
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
            builder.setTitle(medicine.getName());
            builder.setMessage(
                    "藥物名稱: " + "\n" +  medicine.getName() + "\n" +
                            "許可證字號: " + "\n" +  medicine.getAtccode() + "\n" +
                            "製造公司: " + "\n" +   medicine.getIndications()  + "\n" +
                            "適應症: " + "\n" +  medicine.getManufacturer() + "\n" +
                            "形狀: " + medicine.getShape() + "\n" +
                            "顏色: " + medicine.getColor() + "\n" +
                            "標記: " + medicine.getMark() );
            builder.setPositiveButton("確定", null);
            builder.show();
        }
    }
}
