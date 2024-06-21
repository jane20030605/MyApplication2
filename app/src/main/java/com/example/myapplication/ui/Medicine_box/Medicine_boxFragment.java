package com.example.myapplication.ui.Medicine_box;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.myapplication.R;
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

public class Medicine_boxFragment extends Fragment {
    private RecyclerView recyclerView;
    private MedicineBoxAdapter adapter;
    private List<Medicine> medicineList;
    private SwipeRefreshLayout swipeRefreshLayout;

    private static final String MEDICINE_BOX_URL = "http://100.96.1.3/api_get_my_medication.php";
    private static final String IMAGE_BASE_URL = "http://100.96.1.3/medimage/";


    public static Medicine_boxFragment newInstance() {
        return new Medicine_boxFragment();
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_medicine_box, container, false);

        // 初始化 SwipeRefreshLayout
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this::loadMedicineData);

        // 初始化 RecyclerView
        recyclerView = view.findViewById(R.id.medicine_list);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        // 初始化空的藥品列表
        medicineList = new ArrayList<>();

        // 加載藥品數據
        loadMedicineData();

        return view;
    }

    private void loadMedicineData() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        String account = sharedPreferences.getString("ACCOUNT", "");

        if (account.isEmpty()) {
            Log.e("Medicine_boxFragment", "帳戶資訊為空");
            return;
        }

        Log.d("Medicine_boxFragment", "開始發送請求，帳戶: " + account);

        swipeRefreshLayout.post(() -> {
            swipeRefreshLayout.setRefreshing(true); // 在主線程中設置刷新狀態
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // 創建URL對象
                    URL url = new URL(MEDICINE_BOX_URL + "?account=" + account);

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
                        parseMedicineData(jsonArray);

                    } else { // 錯誤響應
                        Log.e("Medicine_boxFragment", "GET 請求失敗. 響應碼: " + responseCode);
                    }

                    // 斷開連接
                    connection.disconnect();

                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                    Log.e("Medicine_boxFragment", "網路請求或JSON解析錯誤: " + e.getMessage());
                } finally {
                    getActivity().runOnUiThread(() -> {
                        swipeRefreshLayout.setRefreshing(false); // 在主線程中停止刷新動畫
                    });
                }
            }
        }).start();
    }


    private void parseMedicineData(JSONArray dataArray) {
        getActivity().runOnUiThread(() -> {
            try {
                // 清空現有列表
                medicineList.clear();

                // 遍歷JSON數組，解析每個藥物數據
                for (int i = 0; i < dataArray.length(); i++) {
                    JSONObject medication = dataArray.getJSONObject(i);
                    Log.d("Medicine_boxFragment", "解析第 " + i + " 條藥物數據: " + medication.toString());

                    String atccode = medication.getString("atccode");
                    String drugName = medication.getString("drug_name");
                    String manufacturer = medication.getString("manufacturer");
                    String imageUrl = IMAGE_BASE_URL + atccode + ".JPG";
                    String timee = medication.getString("timee");
                    String num = medication.getString("num");

                    // 檢查是否已經存在相同 atccode 的藥物
                    boolean found = false;
                    for (Medicine existingMedicine : medicineList) {
                        if (existingMedicine.getAtccode().equals(atccode)) {
                            // 已存在，查找是否有重複的時間，並覆蓋已有的用藥時間和數量
                            boolean timeFound = false;
                            for (int j = 0; j < existingMedicine.getTimeList().size(); j++) {
                                if (existingMedicine.getTimeList().get(j).equals(timee)) {
                                    // 找到重複的時間，覆蓋數量
                                    existingMedicine.getNumList().set(j, num);
                                    timeFound = true;
                                    break;
                                }
                            }
                            if (!timeFound) {
                                // 沒有找到重複的時間，添加新的時間和數量
                                existingMedicine.addDose(timee, num);
                            }
                            found = true;
                            break;
                        }
                    }

                    if (!found) {
                        // 不存在，創建新的藥物對象並添加到列表中
                        Medicine newMedicine = new Medicine(drugName, imageUrl, atccode, manufacturer, timee, num);
                        medicineList.add(newMedicine);
                    }
                }

                Log.d("Medicine_boxFragment", "共解析到 " + medicineList.size() + " 條藥物數據");

                // 更新 RecyclerView 適配器
                if (adapter == null) {
                    adapter = new MedicineBoxAdapter(medicineList);
                    recyclerView.setAdapter(adapter);
                } else {
                    adapter.notifyDataSetChanged();
                }

            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("Medicine_boxFragment", "JSON 解析數據時出錯: " + e.getMessage());
            }
        });
    }


    private void showErrorDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("錯誤");
        builder.setMessage(message);
        builder.setPositiveButton("確定", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static class Medicine {
        private final String name;
        private final String imageUrl;
        private final String atccode;
        private final String manufacturer;
        private final List<String> timeList;
        private final List<String> numList;

        public Medicine(String name, String imageUrl, String atccode, String manufacturer, String timee, String num) {
            this.name = name;
            this.imageUrl = imageUrl;
            this.atccode = atccode;
            this.manufacturer = manufacturer;
            this.timeList = new ArrayList<>();
            this.numList = new ArrayList<>();
            addDose(timee, num);
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

        public List<String> getTimeList() {
            return timeList;
        }

        public List<String> getNumList() {
            return numList;
        }

        public void addDose(String timee, String num) {
            timeList.add(timee);
            numList.add(num);
        }
    }

    class MedicineBoxAdapter extends RecyclerView.Adapter<MedicineBoxAdapter.ViewHolder> {
        private final List<Medicine> medicineList;

        public MedicineBoxAdapter(List<Medicine> medicineList) {
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
            Medicine medicine = medicineList.get(position);
            holder.bind(medicine);
        }

        @Override
        public int getItemCount() {
            return medicineList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private final ImageView imageView;
            private final TextView textView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.medicine_image);
                textView = itemView.findViewById(R.id.medicine_name);
                itemView.setOnClickListener(this);
            }

            public void bind(Medicine medicine) {
                Picasso.get().load(medicine.getImageUrl()).into(imageView);
                textView.setText(medicine.getName());
            }

            @Override
            public void onClick(View v) {
                int position = getAdapterPosition();
                Medicine medicine = medicineList.get(position);
                showMedicineDetailDialog(medicine);
            }
        }
    }

    private void showMedicineDetailDialog(Medicine medicine) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(medicine.getName());

        // 構建藥物詳情文本
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("許可證字號:\n").append(medicine.getAtccode()).append("\n");
        messageBuilder.append("藥物名稱:\n").append(medicine.getName()).append("\n");

        // 添加所有用藥時間和數量到彈窗消息中
        List<String> timeList = medicine.getTimeList();
        List<String> numList = medicine.getNumList();
        for (int i = 0; i < timeList.size(); i++) {
            String timee = timeList.get(i);
            String num = numList.get(i);
            messageBuilder.append("吃藥時間").append(i + 1).append(": ").append(timee).append("，數量: ").append(num).append("\n");
        }

        // 設置彈窗消息內容
        builder.setMessage(messageBuilder.toString());

        // 新增刪除按鈕
        builder.setNegativeButton("刪除", (dialog, which) -> showDeleteConfirmationDialog(medicine));

        // 新增更新按鈕
        builder.setNeutralButton("更新", (dialog, which) -> showUpdateDialog(medicine));

        builder.setPositiveButton("確定", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void showDeleteConfirmationDialog(Medicine medicine) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("刪除確認");

        // 構建刪除確認消息
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("請選擇要刪除的用藥時間和數量:\n");

        // 添加所有用藥時間和數量到確認彈窗中
        List<String> timeList = medicine.getTimeList();
        List<String> numList = medicine.getNumList();
        boolean[] checkedItems = new boolean[timeList.size()];
        String[] timeArray = new String[timeList.size()];
        for (int i = 0; i < timeList.size(); i++) {
            timeArray[i] = timeList.get(i) + "，數量: " + numList.get(i);
        }

        builder.setMultiChoiceItems(timeArray, checkedItems, (dialog, which, isChecked) -> {
            checkedItems[which] = isChecked;
        });

        builder.setPositiveButton("刪除", (dialog, which) -> {
            List<String> timesToDelete = new ArrayList<>();
            for (int i = 0; i < checkedItems.length; i++) {
                if (checkedItems[i]) {
                    timesToDelete.add(timeList.get(i));
                }
            }
            deleteSelectedTimes(medicine, timesToDelete);
        });

        builder.setNegativeButton("取消", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void showUpdateDialog(Medicine medicine) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("更新數量");

        // 構建更新確認消息
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("請選擇要更新的用藥時間和數量:\n");

        // 將所有用藥時間和數量添加到確認對話框中
        List<String> timeList = medicine.getTimeList();
        List<String> numList = medicine.getNumList();
        String[] timeArray = new String[timeList.size()];
        for (int i = 0; i < timeList.size(); i++) {
            timeArray[i] = timeList.get(i) + "，數量: " + numList.get(i);
        }

        builder.setSingleChoiceItems(timeArray, -1, (dialog, which) -> {
            // 關閉對話框
            dialog.dismiss();
            // 顯示輸入對話框以更新選定的數量
            showUpdateQuantityDialog(medicine, timeList.get(which));
        });

        builder.setNegativeButton("取消", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showUpdateQuantityDialog(Medicine medicine, String selectedTime) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("更新數量");

        // 添加一個輸入欄位
        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        builder.setPositiveButton("更新", (dialog, which) -> {
            String newQuantity = input.getText().toString();
            if (!newQuantity.isEmpty()) {
                updateMedicineQuantity(medicine, selectedTime, newQuantity);

            } else {
                Toast.makeText(getContext(), "請輸入有效的數量", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("取消", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteSelectedTimes(Medicine medicine, List<String> timesToDelete) {
        if (timesToDelete.isEmpty()) {
            return;
        }

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        String account = sharedPreferences.getString("ACCOUNT", "");

        for (String timee : timesToDelete) {
            new Thread(() -> {
                try {
                    URL url = new URL("http://100.96.1.3/api_delete_my_medication.php");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/json; utf-8");
                    connection.setRequestProperty("Accept", "application/json");

                    JSONObject jsonInput = new JSONObject();
                    jsonInput.put("drugName", medicine.getName());
                    jsonInput.put("timee", timee);
                    jsonInput.put("account", account);

                    connection.setDoOutput(true);
                    connection.getOutputStream().write(jsonInput.toString().getBytes("utf-8"));

                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        String inputLine;
                        StringBuilder response = new StringBuilder();
                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        in.close();
                        Log.d("Medicine_boxFragment", "刪除成功: " + response.toString());
                    } else {
                        Log.e("Medicine_boxFragment", "刪除失敗，響應碼: " + responseCode);
                    }
                    connection.disconnect();
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                    Log.e("Medicine_boxFragment", "刪除請求錯誤: " + e.getMessage());
                }
            }).start();
        }
    }
    private void updateMedicineQuantity(Medicine medicine, String selectedTime, String newQuantity) {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        String account = sharedPreferences.getString("ACCOUNT", "");

        new Thread(() -> {
            try {
                URL url = new URL("http://100.96.1.3/api_edit_my_medication.php");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json; utf-8");
                connection.setRequestProperty("Accept", "application/json");

                JSONObject jsonInput = new JSONObject();
                jsonInput.put("drugName", medicine.getName());
                jsonInput.put("timee", selectedTime);
                jsonInput.put("num", newQuantity);
                jsonInput.put("account", account);

                connection.setDoOutput(true);
                connection.getOutputStream().write(jsonInput.toString().getBytes("utf-8"));

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String inputLine;
                    StringBuilder response = new StringBuilder();
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                    Log.d("Medicine_boxFragment", "更新成功: " + response.toString());
                    loadMedicineData(); // 重新加載數據以刷新UI
                    // 更新成功，顯示更新成功的對話框
                    getActivity().runOnUiThread(() -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle("更新成功");
                        builder.setMessage("成功更新數量為 " + newQuantity);
                        builder.setPositiveButton("確定", (dialog, which) -> {
                            dialog.dismiss();
                            loadMedicineData(); // 重新加載數據以刷新UI
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    });

                } else {
                    Log.e("Medicine_boxFragment", "更新失敗，響應碼: " + responseCode);
                }
                connection.disconnect();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                Log.e("Medicine_boxFragment", "更新請求錯誤: " + e.getMessage());
            }
        }).start();
    }

}
