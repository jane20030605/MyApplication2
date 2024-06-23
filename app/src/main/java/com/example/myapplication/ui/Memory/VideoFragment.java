package com.example.myapplication.ui.Memory;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class VideoFragment extends Fragment {

    private static final String TAG = "VideoFragment";
    private WebView webView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // 加載 fragment_video.xml 作為此 Fragment 的布局
        return inflater.inflate(R.layout.fragment_video, container, false);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 找到 WebView 並啟用 JavaScript
        webView = view.findViewById(R.id.web_view);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // 啟用縮放控制
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);

        // 自適應屏幕
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);

        // 設置 WebViewClient 以處理頁面加載和錯誤
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Log.d(TAG, "開始加載網頁: " + url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.d(TAG, "網頁加載完成: " + url);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                Log.e(TAG, "加載網頁出錯: " + description);
            }
        });

        // 設置 WebChromeClient 以處理 JavaScript 對話框
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, android.webkit.JsResult result) {
                Log.d(TAG, "JavaScript Alert: " + message);
                return super.onJsAlert(view, url, message, result);
            }

            @Override
            public boolean onJsConfirm(WebView view, String url, String message, android.webkit.JsResult result) {
                Log.d(TAG, "JavaScript Confirm: " + message);
                return super.onJsConfirm(view, url, message, result);
            }

            @Override
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, android.webkit.JsPromptResult result) {
                Log.d(TAG, "JavaScript Prompt: " + message);
                return super.onJsPrompt(view, url, message, defaultValue, result);
            }
        });

        // 加載動態生成的圖像或視頻流 URL
        String streamUrl = "http://100.96.1.2:5000/video_feed"; // 替換為你的流媒體 URL
        Log.d(TAG, "開始加載視訊流: " + streamUrl);
        webView.loadUrl(streamUrl);
        webView.setWebViewClient(new MyWebViewClient());

    }

    @Override
    public void onDestroyView() {
        // 避免 WebView 活動在 Fragment 被銷毀時仍在運行
        if (webView != null) {
            webView.destroy();
            Log.d(TAG, "WebView 已銷毀");
        }
        super.onDestroyView();
    }

    // 自定義 WebViewClient 類來處理 multipart/x-mixed-replace 格式的視訊流
    private class MyWebViewClient extends WebViewClient {
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
            // 檢查是否為視訊流 URL
            String url = request.getUrl().toString();
            if (url.startsWith("http://100.96.1.2:5000/video_feed")) {
                try {
                    // 打開連線並獲取視訊流資源
                    Log.d(TAG, "處理視訊流請求: " + url);
                    URL videoUrl = new URL(url);
                    URLConnection connection = videoUrl.openConnection();
                    connection.connect();

                    // 檢查 Content-Type 是否為 multipart/x-mixed-replace
                    String contentType = connection.getContentType();
                    if (contentType != null && contentType.startsWith("multipart/x-mixed-replace")) {
                        // 返回處理後的資源流
                        InputStream inputStream = connection.getInputStream();
                        Log.d(TAG, "成功獲取視訊流資源");
                        // 注意：這裡不返回 WebResourceResponse，而是設置視訊流處理
                        handleMultipartXMixedReplaceStream(inputStream);
                        return null; // 返回 null，因為不使用 WebResourceResponse 來處理這種類型的資源
                    } else {
                        Log.e(TAG, "不支援的內容類型: " + contentType);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "獲取視訊流資源失敗: " + e.getMessage());
                }
            }
            return super.shouldInterceptRequest(view, request);
        }

        // 處理 multipart/x-mixed-replace 格式的視訊流
        private void handleMultipartXMixedReplaceStream(InputStream inputStream) {
            // 在後台執行此邏輯以避免主線程阻塞
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        // 讀取視訊流部分
                        byte[] boundaryBytes = "\r\n".getBytes(); // 分隔每個部分的邊界
                        byte[] headerBytes = ("Content-Type: image/jpeg" + "\r\n\r\n"+ "frame_bytes" + "\r\n").getBytes(); // JPEG 圖像的標頭
                        int headerLength = headerBytes.length;
                        byte[] buffer = new byte[1024];
                        int bytesRead;

                        // 連續讀取視訊流
                        while (true) {
                            // 尋找每個部分的開始
                            int endIndex = findBytes(inputStream, boundaryBytes);
                            if (endIndex == -1) {
                                // 未找到分隔符號，退出迴圈
                                break;
                            }
                            // 跳過分隔符號
                            inputStream.skip(boundaryBytes.length);

                            // 找到 JPEG 圖像的開始位置
                            int startIndex = findBytes(inputStream, headerBytes);
                            if (startIndex == -1) {
                                // 未找到 JPEG 圖像的標頭，退出迴圈
                                break;
                            }

                            // 讀取 JPEG 圖像
                            inputStream.skip(headerLength); // 跳過標頭
                            ByteArrayOutputStream imageBuffer = new ByteArrayOutputStream();
                            while ((bytesRead = inputStream.read(buffer)) != -1) {
                                if (isEndOfImage(buffer, bytesRead, boundaryBytes)) {
                                    // 到達 JPEG 圖像的結束
                                    imageBuffer.write(buffer, 0, bytesRead - boundaryBytes.length);
                                    break;
                                } else {
                                    imageBuffer.write(buffer, 0, bytesRead);
                                }
                            }

                            // 轉換為位圖
                            byte[] imageBytes = imageBuffer.toByteArray();
                            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

                            // 在主線程更新 WebView
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // 將位圖顯示在 WebView 中
                                    if (webView != null) {
                                        String base64Image = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                                        String imageHtml = "<html><body><img src=\"data:image/jpeg;base64," + base64Image + "\" width=\"100%\"/></body></html>";
                                        webView.loadData(imageHtml, "text/html", "utf-8");
                                    }
                                }
                            });
                            // 添加延遲以控制抓取速度
                            try {
                                Thread.sleep(10); // 10毫秒的延遲，可以根據需要調整
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            // 跳過分隔符號以準備下一個部分
                            inputStream.skip(boundaryBytes.length);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        // 尋找指定的位元組在輸入流中的位置
        private int findBytes(InputStream inputStream, byte[] bytes) throws IOException {
            int index = 0;
            int b;
            while ((b = inputStream.read()) != -1) {
                if (b == bytes[index]) {
                    index++;
                    if (index == bytes.length) {
                        return index;
                    }
                } else {
                    index = 0;
                }
            }
            return -1;
        }

        // 檢查是否到達 JPEG 圖像的結束
        private boolean isEndOfImage(byte[] buffer, int bytesRead, byte[] boundaryBytes) {
            if (bytesRead >= boundaryBytes.length) {
                for (int i = 0; i < boundaryBytes.length; i++) {
                    if (buffer[bytesRead - boundaryBytes.length + i] != boundaryBytes[i]) {
                        return false;
                    }
                }
                return true;
            }
            return false;
        }
    }

}

