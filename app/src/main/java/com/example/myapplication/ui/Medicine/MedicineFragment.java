package com.example.myapplication.ui.Medicine;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.myapplication.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MedicineFragment extends Fragment {

    private static final String TAG = MedicineFragment.class.getSimpleName();
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final String API_URL = "http://100.96.1.3/api_query_medication.php";

    private EditText editTextLicenseNumber; // 輸入許可證號碼的編輯框
    private EditText editTextChineseName; // 輸入中文名的編輯框
    private EditText editTextMark; // 輸入標記的編輯框
    private Spinner spinnerLicenseNumber; // 許可證號碼下拉框
    private Spinner spinnerShape; // 形狀下拉框
    private Spinner spinnerNotch; // 缺口下拉框
    private Spinner spinnerColor; // 顏色下拉框
    private Spinner spinnerSymbol; // 符號下拉框

    public MedicineFragment() {
        super(R.layout.fragment_medicine);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 初始化介面元件
        editTextLicenseNumber = view.findViewById(R.id.editText_licenseNumber); // 許可證號碼輸入框
        editTextChineseName = view.findViewById(R.id.editText_chineseName); // 中文名輸入框
        editTextMark = view.findViewById(R.id.editText_mark); // 標記輸入框
        spinnerLicenseNumber = view.findViewById(R.id.spinner_licenseNumber); // 許可證號碼下拉框
        spinnerShape = view.findViewById(R.id.spinner_shape); // 形狀下拉框
        spinnerNotch = view.findViewById(R.id.spinner_notch); // 缺口下拉框
        spinnerColor = view.findViewById(R.id.spinner_color); // 顏色下拉框
        spinnerSymbol = view.findViewById(R.id.spinner_symbol); // 符號下拉框
        Button queryButton = view.findViewById(R.id.query_button); // 查詢按鈕
        Button medicineAllBoxButton = view.findViewById(R.id.medicine_allbox_button); // 藥物總覽按鈕

        // 設置下拉菜單選項
        ArrayAdapter<CharSequence> licenseAdapter = ArrayAdapter.createFromResource(
                requireContext(), R.array.license_numbers, android.R.layout.simple_spinner_item);
        licenseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLicenseNumber.setAdapter(licenseAdapter); // 設置許可證號碼下拉框適配器

        ArrayAdapter<CharSequence> shapeAdapter = ArrayAdapter.createFromResource(
                requireContext(), R.array.shapes, android.R.layout.simple_spinner_item);
        shapeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerShape.setAdapter(shapeAdapter); // 設置形狀下拉框適配器

        ArrayAdapter<CharSequence> notchAdapter = ArrayAdapter.createFromResource(
                requireContext(), R.array.notches, android.R.layout.simple_spinner_item);
        notchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerNotch.setAdapter(notchAdapter); // 設置缺口下拉框適配器

        ArrayAdapter<CharSequence> colorAdapter = ArrayAdapter.createFromResource(
                requireContext(), R.array.colors, android.R.layout.simple_spinner_item);
        colorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerColor.setAdapter(colorAdapter); // 設置顏色下拉框適配器

        ArrayAdapter<CharSequence> symbolAdapter = ArrayAdapter.createFromResource(
                requireContext(), R.array.symbols, android.R.layout.simple_spinner_item);
        symbolAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSymbol.setAdapter(symbolAdapter); // 設置符號下拉框適配器

        // 設置查詢按鈕點擊事件
        queryButton.setOnClickListener(v -> {
            String licenseNumber = spinnerLicenseNumber.getSelectedItem().toString() + editTextLicenseNumber.getText().toString().trim();
            String chineseName = editTextChineseName.getText().toString().trim();
            String shape = spinnerShape.getSelectedItem().toString();
            String notch = spinnerNotch.getSelectedItem().toString();
            String color = spinnerColor.getSelectedItem().toString();
            String symbol = spinnerSymbol.getSelectedItem().toString();
            String mark = editTextMark.getText().toString().trim();

            // 將空白選項設置為空字串
            if (licenseNumber.equals("請選擇證別")) {
                licenseNumber = "";
            }
            if (shape.equals("請選擇")) {
                shape = "";
            }
            if (color.equals("請選擇")) {
                color = "";
            }
            if (symbol.equals("請選擇")) {
                symbol = "";
            }
            if (notch.equals("請選擇")) {
                notch = "";
            }

            // 執行查詢
            Log.d(TAG, "查詢按鈕點擊");
            performMedicationQuery(licenseNumber, chineseName, shape, notch, color, symbol, mark);
        });

        // 設置藥物總覽按鈕點擊事件
        medicineAllBoxButton.setOnClickListener(v -> {
            // 導航到藥物總覽頁面，這裡假設使用 Navigation Component
            Navigation.findNavController(view).navigate(R.id.nav_medicine_allbox);
        });
    }

    private void performMedicationQuery(String licenseNumber, String chineseName, String shape,
                                        String notch, String color, String symbol, String mark) {
        new MedicationQueryTask(licenseNumber, chineseName, shape, notch, color, symbol, mark).execute();
    }

    @SuppressLint("StaticFieldLeak")
    private class MedicationQueryTask extends AsyncTask<Void, Void, String> {
        private final String licenseNumber;
        private final String chineseName;
        private final String shape;
        private final String notch;
        private final String color;
        private final String symbol;
        private final String mark;

        public MedicationQueryTask(String licenseNumber, String chineseName, String shape,
                                   String notch, String color, String symbol, String mark) {
            this.licenseNumber = licenseNumber;
            this.chineseName = chineseName;
            this.shape = shape;
            this.notch = notch;
            this.color = color;
            this.symbol = symbol;
            this.mark = mark;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(TAG, "執行藥物查詢：準備執行前");
        }

        @Override
        protected String doInBackground(Void... voids) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS)
                    .build();

            JSONObject requestData = new JSONObject();
            try {
                requestData.put("s_licence", licenseNumber);
                requestData.put("s_cname", chineseName);
                requestData.put("s_shape", shape);
                requestData.put("s_color", color);
                requestData.put("s_mark", mark);
                requestData.put("s_nick", notch);
                requestData.put("s_strip", symbol);

                Log.d(TAG, "執行藥物查詢：傳遞的數據：" + requestData.toString());
            } catch (JSONException e) {
                e.printStackTrace();
                return "JSON組建失敗: " + e.getMessage();
            }

            RequestBody body = RequestBody.create(JSON, requestData.toString());
            Request request = new Request.Builder()
                    .url(API_URL)
                    .post(body)
                    .build();

            try {
                Log.d(TAG, "執行藥物查詢：發送請求至服務器");
                Response response = client.newCall(request).execute();
                if (!response.isSuccessful()) {
                    Log.d(TAG, "執行藥物查詢：HTTP 錯誤碼: " + response.code());
                    return "HTTP 錯誤碼: " + response.code();
                }

                String jsonResponse = response.body().string();
                Log.d(TAG, "執行藥物查詢：接收到的響應：" + jsonResponse);
                return jsonResponse;
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "執行藥物查詢：查詢失敗: " + e.getMessage());
                return "查詢失敗: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d(TAG, "執行藥物查詢：處理結果");

            try {
                JSONObject jsonResponse = new JSONObject(result);
                String message = jsonResponse.getString("message");
                Log.d(TAG, "執行藥物查詢：服務器返回的消息: " + message);

                if (message.equals("查詢成功")) {
                    JSONArray dataArray = jsonResponse.getJSONArray("data");
                    Log.d(TAG, "執行藥物查詢：接收到的數據: " + dataArray.toString());
                    Toast.makeText(requireContext(), "查詢成功", Toast.LENGTH_SHORT).show();
                    
                    // 查詢成功後導航到結果界面
                    // 將JSON數組打包為字符串
                    Bundle bundle = new Bundle();
                    bundle.putString("searchResults", dataArray.toString());

                    // 使用打包的數據進行導航到Medicine_inquireFragment
                    Navigation.findNavController(requireView())
                            .navigate(R.id.nav_medicine_inquire, bundle);
                    // TODO: 處理查詢到的數據
                    
                } else {
                    // 查無結果的處理
                    Log.d(TAG, "執行藥物查詢：未找到結果");
                    Toast.makeText(requireContext(), "查無結果", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                Log.e(TAG, "執行藥物查詢：解析失敗: " + e.getMessage(), e);
                Toast.makeText(requireContext(), "查詢失敗，請稍後再試", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
