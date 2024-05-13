package com.example.myapplication;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Main_SQL extends SQLiteOpenHelper {

    // 定義資料庫名稱和版本
    private static final String DATABASE_NAME = "example.db";
    private static final int DATABASE_VERSION = 1;

    // 定義資料表名稱和欄位名稱
    private static final String TABLE_NAME = "memory_table";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TEXT_DATA = "text_data";

    // 建構子
    public Main_SQL(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // 建立資料庫表格
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TEXT_DATA + " TEXT)";
        db.execSQL(createTableQuery);
    }

    // 升級資料庫
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // 從URL讀取資料庫內容並插入到本地資料庫中
    public void downloadAndInsertDataFromUrl() {
        try {
            // 定義URL
            URL url = new URL("http://26.110.164.151/Untitled-1.php");

            // 打開連接
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // 設置請求方式
            connection.setRequestMethod("GET");

            // 讀取輸入流
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            // 讀取服務器回應
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // 斷開連接
            connection.disconnect();

            // 將資料插入到資料庫中
            insertDataIntoDatabase(response.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 將資料插入到資料庫中
    private void insertDataIntoDatabase(String data) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME); // 清空資料表

        String[] lines = data.split("\n");
        for (String line : lines) {
            db.execSQL("INSERT INTO " + TABLE_NAME + " (" + COLUMN_TEXT_DATA + ") VALUES ('" + line + "')");
        }
        db.close();
    }

    // 獲取資料庫中的資料
    public Cursor getDataFromDatabase() {
        SQLiteDatabase database = this.getReadableDatabase();
        return database.query(TABLE_NAME, null, null, null, null, null, null);
    }
}
