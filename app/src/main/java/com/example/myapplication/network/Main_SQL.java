package com.example.myapplication.network;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.myapplication.ui.calender_thing.CalendarEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Main_SQL extends SQLiteOpenHelper {


    public Main_SQL(Context context, String name, SQLiteDatabase.CursorFactory factory,
                    int version) {
        super(context, name, factory, version);
        // TODO Auto-generated constructor stub
    }

    // 定義資料庫名稱和版本
    private static final String DATABASE_NAME = "Main_SQL.db";
    private static final int DATABASE_VERSION = 1;

    // 定義資料表名稱
    private static final String TABLE_NAME_User = "user"; // 註冊登入/使用者資料 資料表
    private static final String TABLE_NAME_Calender = "calender"; // 行事曆資料表
    private static final String TABLE_NAME_Medicine = "medicine"; // 藥物查詢資料表
    private static final String TABLE_NAME_Medicine_reminder = "medicine_reminder"; // 個人藥物資料表
    private static final String TABLE_NAME_Contact_person = "contact_person"; // 緊急連絡人資料表
    private static final String TABLE_NAME_Fall_record = "fall_record"; // 跌倒紀錄資料表

    // 定義資料表欄位名稱
    // 登入註冊使用者欄位
    private static final String COLUMN_ID = "id"; // 使用者帳號ID
    private static final String COLUMN_Account = "account"; // 使用者帳號
    private static final String COLUMN_Password = "password"; // 使用者密碼
    private static final String COLUMN_Name = "name"; // 使用者姓名
    private static final String COLUMN_Tel = "tel"; // 使用者電話
    private static final String COLUMN_Birthday = "birthday"; // 使用者生日
    private static final String COLUMN_Mail = "mail"; // 使用者郵箱
    private static final String COLUMN_Address = "address"; // 使用者住址
    private static final String COLUMN_Reset_token = "reset_token"; // 忘記密碼

    // 行事曆使用者欄位
    private static final String COLUMN_Event_ID = "event_id"; // 行事曆事件ID
    private static final String COLUMN_Thing = "thing"; // 行事曆事件
    private static final String COLUMN_Data_up = "data_up"; // 行事曆起始時間
    private static final String COLUMN_Data_end = "data_end"; // 行事曆結束時間
    private static final String COLUMN_People = "people"; // 行事曆人員
    private static final String COLUMN_Describe = "describe"; // 行事曆內容

    // 緊急連絡人欄位
    private static final String COLUMN_Contact_ID = "contact_id"; // 緊急連絡人ID
    private static final String COLUMN_Contact_name = "contact_name"; // 緊急連絡人姓名
    private static final String COLUMN_Contact_tel = "contact_tel"; // 緊急連絡人電話
    private static final String COLUMN_Relation = "relation"; // 緊急連絡人關係
    private static final String COLUMN_USER_ID = "user_id"; // 使用者id

    // 藥物查詢欄位
    private static final String COLUMN_Med_ID = "med_id"; // 藥物查詢ID
    private static final String COLUMN_Atccode = "atccode"; // 藥物字號名稱
    private static final String COLUMN_Drug_name = "drug_name"; // 藥物名稱
    private static final String COLUMN_Indications = "indications"; // 藥物公司
    private static final String COLUMN_Manufacturer = "manufacturer"; // 藥物適應症
    private static final String COLUMN_Shape = "shape"; // 藥物形狀
    private static final String COLUMN_Color = "color"; // 藥物顏色
    private static final String COLUMN_Mark = "mark"; // 藥物標記
    private static final String COLUMN_Nick = "nick"; // 藥物符號
    private static final String COLUMN_Strip = "strip"; // 藥物刻痕

    // 個人藥物欄位
    private static final String COLUMN_Reminder_ID = "reminder_id"; // 個人藥物ID
    private static final String COLUMN_Timee = "timee"; // 個人藥物時間
    private static final String COLUMN_Num = "num"; // 個人藥物

    // 跌倒紀錄
    private static final String COLUMN_Fall_date = "fall_date"; // 跌倒紀錄資料
    private static final String COLUMN_Video = "video"; // 跌倒紀錄影像

    // 建構子
    public Main_SQL(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    // 建立資料庫表格
    @Override
    public void onCreate(SQLiteDatabase db) {
        // 建立使用者資料註冊資料表
        String createUserTableQuery = "CREATE TABLE " + TABLE_NAME_User + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_Account + " CHAR(12), " +
                COLUMN_Password + " VARCHAR(255), " +
                COLUMN_Name + " CHAR(10), " +
                COLUMN_Tel + " CHAR(10), " +
                COLUMN_Birthday + " DATE, " +
                COLUMN_Mail + " VARCHAR(255), " +
                COLUMN_Address + " CHAR(50), " +
                COLUMN_Reset_token + " VARCHAR(45))";
        db.execSQL(createUserTableQuery);

        // 建立行事曆事件資料表
        String createCalendarTableQuery = "CREATE TABLE " + TABLE_NAME_Calender + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_Thing + " TEXT, " +
                COLUMN_Data_up + " DATETIME, " +
                COLUMN_Data_end + " DATETIME, " +
                COLUMN_People + " TEXT, " +
                COLUMN_Describe + " TEXT, " +
                COLUMN_Account + " TEXT)";
        db.execSQL(createCalendarTableQuery);

        // 建立藥物查詢資料表
        String createMedicineTableQuery = "CREATE TABLE " + TABLE_NAME_Medicine + " (" +
                COLUMN_Med_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_Atccode + "  VARCHAR(45), " +
                COLUMN_Drug_name + " VARCHAR(45), " +
                COLUMN_Indications + " VARCHAR(45), " +
                COLUMN_Manufacturer + " VARCHAR(45), " +
                COLUMN_Shape + " VARCHAR(45), " +
                COLUMN_Color + " VARCHAR(45), " +
                COLUMN_Mark + " VARCHAR(45), " +
                COLUMN_Nick + " VARCHAR(45), " +
                COLUMN_Strip + " VARCHAR(45))";
        db.execSQL(createMedicineTableQuery);

        // 建立個人藥物庫資料表
        String createReminderTableQuery = "CREATE TABLE " + TABLE_NAME_Medicine_reminder + " (" +
                COLUMN_ID + " CHAR(10), " +
                COLUMN_Account + " VARCHAR(45), " +
                COLUMN_Reminder_ID + " INT, " +
                COLUMN_Atccode + " VARCHAR(45), " +
                COLUMN_Drug_name + " VARCHAR(45), " +
                COLUMN_Manufacturer + " VARCHAR(45), " +
                COLUMN_Timee + " VARCHAR(45), " +
                COLUMN_Num + " INT)";
        db.execSQL(createReminderTableQuery);

        // 建立緊急連絡人資料表
        String createEmergencyTableQuery = "CREATE TABLE " + TABLE_NAME_Contact_person + " (" +
                COLUMN_Contact_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_Contact_name + " CHAR(10), " +
                COLUMN_Contact_tel + " CHAR(10), " +
                COLUMN_Relation + " CHAR(45), " +
                COLUMN_USER_ID + " VARCHAR(45), " + // 這個欄位關聯到使用者帳號ID
                "FOREIGN KEY(" + COLUMN_ID + ") REFERENCES " + TABLE_NAME_User + "(" + COLUMN_ID + "))";
        db.execSQL(createEmergencyTableQuery);

        // 建立跌倒紀錄資料表
        String createFallTableQuery = "CREATE TABLE " + TABLE_NAME_Fall_record + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_Account + " TEXT, " +
                COLUMN_Fall_date + " DATETIME, " +
                COLUMN_Video + " BLOB)";
        db.execSQL(createFallTableQuery);
    }


    // 升級資料庫
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_User);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_Calender);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_Medicine);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_Medicine_reminder);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_Contact_person);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_Fall_record);

        // 重新建立所有資料表
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
            connection.setRequestMethod("POST");

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

        } catch (IOException e) {
            e.printStackTrace();
            Log.e("test_tag", "e");
            Log.e("test_tag", "insertDataIntoDatabase failed");
            Log.e("DownloadData", "Failed to download data: " + e.getMessage());

        }
    }

}
