package com.example.myapplication.ui.Memory;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class VideoFragment extends Fragment implements SurfaceHolder.Callback {

    private static final String TAG = "VideoFragment";

    private SurfaceHolder surfaceHolder;
    private boolean running = true; // 記錄視頻流是否運行
    private HttpURLConnection urlConnection;
    private InputStream inputStream;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 載入此片段的佈局檔案
        View view = inflater.inflate(R.layout.fragment_video, container, false);

        // 尋找 SurfaceView 並設置 SurfaceHolder 的回調
        SurfaceView surfaceView = view.findViewById(R.id.surfaceView);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);

        return view;
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        // 當 Surface 創建後，啟動後台任務來獲取並顯示視頻幀
        String videoUrl = "http://100.96.1.3:5000/video_feed/";
        new VideoStreamTask().execute(videoUrl);
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        // 可根據需要處理 Surface 大小改變事件
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        // 當 Surface 被銷毀時，停止後台任務並釋放資源
        running = false;
        if (urlConnection != null) {
            urlConnection.disconnect();
        }
        try {
            if (inputStream != null) {
                inputStream.close();
                Log.d(TAG, "surfaceDestroyed: InputStream 關閉.");
            }
        } catch (IOException e) {
            Log.e(TAG, "surfaceDestroyed: 關閉 InputStream 時出現錯誤: " + e.getMessage());
        }
    }

    private class VideoStreamTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            try {
                // 打開與視頻源 URL 的連接
                URL url = new URL(params[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Connection", "close"); // 避免持久連接
                urlConnection.setConnectTimeout(10000); // 設置連接超時為10秒
                urlConnection.setReadTimeout(10000); // 設置讀取超時為10秒
                inputStream = urlConnection.getInputStream();

                Log.d(TAG, "doInBackground: 連接到視頻流成功.");

                // 讀取視頻幀
                readVideoStream(inputStream);

            } catch (IOException e) {
                Log.e(TAG, "doInBackground: 連接到視頻流時出現錯誤: " + e.getMessage());
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                try {
                    if (inputStream != null) {
                        inputStream.close();
                        Log.d(TAG, "doInBackground: InputStream 關閉.");
                    }
                } catch (IOException e) {
                    Log.e(TAG, "doInBackground: 關閉 InputStream 時出現錯誤: " + e.getMessage());
                }
            }
            return null;
        }

        private void readVideoStream(InputStream inputStream) {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                String boundary = "--frame";

                // 持續讀取直到結束或運行標誌為 false
                while (running && (line = reader.readLine()) != null) {
                    if (line.startsWith(boundary)) {
                        // 讀取圖像數據直到下一個分界線
                        StringBuilder imageData = new StringBuilder();
                        while ((line = reader.readLine()) != null && !line.startsWith(boundary)) {
                            imageData.append(line).append("\r\n");
                        }

                        // 將圖像數據轉換為 Bitmap
                        byte[] imageBytes = imageData.toString().getBytes();
                        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

                        // 在 SurfaceView 上繪製位圖
                        if (bitmap != null) {
                            drawOnSurfaceView(bitmap);
                        }
                    }
                }
            } catch (IOException e) {
                Log.e(TAG, "readVideoStream: 讀取視頻流時出現錯誤: " + e.getMessage());
            }
        }

        private void drawOnSurfaceView(Bitmap bitmap) {
            try {
                Canvas canvas = surfaceHolder.lockCanvas();
                if (canvas != null) {
                    // 調整位圖大小
                    int surfaceWidth = canvas.getWidth();
                    int surfaceHeight = canvas.getHeight();
                    int videoWidth = bitmap.getWidth();
                    int videoHeight = bitmap.getHeight();

                    // 計算縮放比例
                    float scaleX = (float) surfaceWidth / videoWidth;
                    float scaleY = (float) surfaceHeight / videoHeight;

                    Matrix matrix = new Matrix();
                    matrix.postScale(scaleX, scaleY);

                    // 創建縮放後的位圖
                    Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, videoWidth, videoHeight, matrix, true);

                    // 在畫布上繪製位圖
                    canvas.drawBitmap(scaledBitmap, 0, 0, null);

                    // 解鎖並提交畫布
                    surfaceHolder.unlockCanvasAndPost(canvas);

                    Log.d(TAG, "drawOnSurfaceView: 位圖繪製完成.");
                }
            } catch (Exception e) {
                Log.e(TAG, "drawOnSurfaceView: 在 SurfaceView 上繪製位圖時出現錯誤: " + e.getMessage());
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        running = false;
        if (urlConnection != null) {
            urlConnection.disconnect();
        }
        try {
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
