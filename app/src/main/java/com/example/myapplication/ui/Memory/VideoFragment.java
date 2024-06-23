package com.example.myapplication.ui.Memory;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
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

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class VideoFragment extends Fragment implements SurfaceHolder.Callback {

    private static final String TAG = "VideoFragment";

    private SurfaceHolder surfaceHolder;
    private boolean running = true;

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
        // Surface 創建後，啟動後台任務來獲取並顯示視頻幀
        String videoUrl = "http://100.96.1.3:5000/video_feed/";
        new VideoStreamTask().execute(videoUrl);
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        // Surface 大小改變時，如果需要處理，請在此處處理
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        // Surface 被銷毀時，停止後台任務並釋放資源
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

    @SuppressLint("StaticFieldLeak")
    private class VideoStreamTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            int retryCount = 0;
            final int maxRetries = 3;

            while (retryCount < maxRetries && running && !isCancelled()) {
                try {
                    // 打開與視頻源URL的連接
                    URL url = new URL(params[0]);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestProperty("Connection", "close"); // 避免持久連接
                    inputStream = urlConnection.getInputStream();

                    Log.d(TAG, "doInBackground: Connected to video stream.");

                    // 持續讀取視頻幀直到停止
                    while (running) {
                        if (isCancelled()) {
                            break;
                        }

                        // 從輸入流解碼位圖
                        Bitmap bitmap = readNextFrame(inputStream);
                        if (bitmap != null) {
                            // 在 SurfaceView 上繪製位圖
                            drawOnSurfaceView(bitmap);
                        }
                    }

                } catch (IOException e) {
                    Log.e(TAG, "doInBackground: Error reading stream: " + e.getMessage());
                    e.printStackTrace();

                    retryCount++;
                    Log.d(TAG, "doInBackground: Retrying... Attempt " + retryCount);

                    // Delay before retrying
                    try {
                        Thread.sleep(1000); // 1 second delay
                    } catch (InterruptedException ie) {
                        ie.printStackTrace();
                    }

                } finally {
                    // Close connection and release resources
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                    try {
                        if (inputStream != null) {
                            inputStream.close();
                            Log.d(TAG, "doInBackground: InputStream closed.");
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "doInBackground: Error closing inputStream: " + e.getMessage());
                    }
                }
            }

            return null;
        }

        private Bitmap readNextFrame(InputStream inputStream) throws IOException {
            Bitmap frameBitmap = null;
            boolean foundBoundary = false;

            // 用來標記分界線的字符串
            String boundary = "--frame";

            // 讀取每一個部分直到找到下一個分界線
            while (!foundBoundary && running) {
                String line = readLine(inputStream);

                // 找到分界線，開始處理下一幀圖像
                if (line.startsWith(boundary)) {
                    // 讀取圖像數據，這裡假設是 jpeg 格式
                    StringBuilder imageData = new StringBuilder();
                    while ((line = readLine(inputStream)) != null && !line.startsWith(boundary)) {
                        imageData.append(line).append("\r\n");
                    }

                    // 將數據轉換為 Bitmap
                    byte[] imageBytes = imageData.toString().getBytes();
                    frameBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                }
            }

            return frameBitmap;
        }

        private String readLine(InputStream inputStream) throws IOException {
            StringBuilder stringBuilder = new StringBuilder();
            int value;
            while ((value = inputStream.read()) != -1) {
                char c = (char) value;
                if (c == '\n') {
                    break;
                }
                stringBuilder.append(c);
            }
            return stringBuilder.toString().trim();
        }

        private void drawOnSurfaceView(Bitmap bitmap) {
            try {
                // 鎖定 SurfaceView 的畫布並繪製位圖
                Canvas canvas = surfaceHolder.lockCanvas();
                if (canvas != null) {
                    canvas.drawBitmap(bitmap, 0, 0, null);
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
