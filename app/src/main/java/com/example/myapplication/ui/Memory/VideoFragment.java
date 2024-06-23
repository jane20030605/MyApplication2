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

import java.io.BufferedInputStream;
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
    private static final String BOUNDARY_PREFIX = "--frame";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video, container, false);
        SurfaceView surfaceView = view.findViewById(R.id.surfaceView);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        return view;
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        String videoUrl = "http://26.136.217.149:5000/video_feed";
        new VideoStreamTask().execute(videoUrl);
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        Log.d(TAG, "Surface 大小已更改，新大小：" + width + " x " + height);
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        stopStream();
    }

    private void stopStream() {
        running = false;
        if (urlConnection != null) {
            urlConnection.disconnect();
            Log.d(TAG, "已斷開視訊流的連接.");
        }
        try {
            if (inputStream != null) {
                inputStream.close();
                Log.d(TAG, "InputStream 關閉.");
            }
        } catch (IOException e) {
            Log.e(TAG, "關閉 InputStream 時出現錯誤: " + e.getMessage());
        }
    }

    private class VideoStreamTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                Log.d(TAG, "連接到視頻流 URL: " + url.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Connection", "close");
                urlConnection.setConnectTimeout(300000);
                urlConnection.setReadTimeout(300000);
                urlConnection.setRequestProperty("Accept", "multipart/x-mixed-replace");

                inputStream = new BufferedInputStream(urlConnection.getInputStream());
                Log.d(TAG, "連接到視頻流成功.");

                readVideoStream(inputStream);

            } catch (IOException e) {
                Log.e(TAG, "連接到視頻流時出現錯誤: " + e.getMessage());
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                    Log.d(TAG, "已斷開視訊流的連接.");
                }
                try {
                    if (inputStream != null) {
                        inputStream.close();
                        Log.d(TAG, "InputStream 關閉.");
                    }
                } catch (IOException e) {
                    Log.e(TAG, "關閉 InputStream 時出現錯誤: " + e.getMessage());
                }
            }
            return null;
        }

        private void readVideoStream(InputStream inputStream) {
            try {
                byte[] boundaryBytes = BOUNDARY_PREFIX.getBytes();
                int boundaryLength = boundaryBytes.length;

                while (running) {
                    StringBuilder header = new StringBuilder();
                    int current;
                    while ((current = inputStream.read()) != -1) {
                        header.append((char) current);
                        int headerLength = header.length();
                        if (headerLength >= boundaryLength) {
                            boolean isBoundary = true;
                            for (int i = 0; i < boundaryLength; i++) {
                                if (header.charAt(headerLength - boundaryLength + i) != boundaryBytes[i]) {
                                    isBoundary = false;
                                    break;
                                }
                            }
                            if (isBoundary) {
                                break;
                            }
                        }
                    }

                    StringBuilder imageData = new StringBuilder();
                    while ((current = inputStream.read()) != -1) {
                        imageData.append((char) current);
                        int imageDataLength = imageData.length();
                        if (imageDataLength >= boundaryLength + 4) {
                            boolean isBoundary = true;
                            for (int i = 0; i < boundaryLength; i++) {
                                if (imageData.charAt(imageDataLength - boundaryLength + i) != boundaryBytes[i]) {
                                    isBoundary = false;
                                    break;
                                }
                            }
                            if (isBoundary) {
                                break;
                            }
                        }
                    }

                    byte[] imageBytes = imageData.toString().getBytes();
                    Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                    if (bitmap != null) {
                        drawOnSurfaceView(bitmap);
                    }
                }
            } catch (IOException e) {
                Log.e(TAG, "讀取視頻流時出現錯誤: " + e.getMessage());
            }
        }

        private void drawOnSurfaceView(Bitmap bitmap) {
            Canvas canvas = surfaceHolder.lockCanvas();
            if (canvas != null) {
                try {
                    canvas.drawColor(0, android.graphics.PorterDuff.Mode.CLEAR);
                    int surfaceWidth = canvas.getWidth();
                    int surfaceHeight = canvas.getHeight();
                    int videoWidth = bitmap.getWidth();
                    int videoHeight = bitmap.getHeight();

                    float scaleX = (float) surfaceWidth / videoWidth;
                    float scaleY = (float) surfaceHeight / videoHeight;

                    Matrix matrix = new Matrix();
                    matrix.postScale(scaleX, scaleY);

                    Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, videoWidth, videoHeight, matrix, true);

                    canvas.drawBitmap(scaledBitmap, 0, 0, null);
                    Log.d(TAG, "位圖繪製完成.");
                } catch (Exception e) {
                    Log.e(TAG, "在 SurfaceView 上繪製位圖時出現錯誤: " + e.getMessage());
                } finally {
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopStream();
    }
}
