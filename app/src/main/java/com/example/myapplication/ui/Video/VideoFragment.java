package com.example.myapplication.ui.Video;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.myapplication.databinding.FragmentVideoBinding;
import com.google.common.util.concurrent.ListenableFuture;
import com.bumptech.glide.Glide;
import java.io.File;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class VideoFragment extends Fragment {

    // ViewModel 與 View Binding 變數
    private VideoViewModel mViewModel;
    private FragmentVideoBinding binding;
    private static final int REQUEST_CAMERA_PERMISSION = 1001;
    private Camera camera;
    private ImageCapture imageCapture;

    // 建立新實例的方法
    public static VideoFragment newInstance() {
        return new VideoFragment();
    }

    // 創建視圖
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // 初始化 View Binding
        binding = FragmentVideoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    // 當活動被創建時調用
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // 初始化 ViewModel
        mViewModel = new ViewModelProvider(this).get(VideoViewModel.class);

        // 檢查相機權限
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // 請求相機權限
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        } else {
            // 如果已經有權限，啟動相機
            startCamera();
        }

        // 初始化縮放手勢檢測器
        initializeZoom();

        // 設置拍照按鈕點擊事件
        binding.captureButton.setOnClickListener(v -> takePhoto());
    }

    // 啟動相機的方法
    private void startCamera() {
        // 獲取相機提供者
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext());
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                // 綁定預覽和拍照
                bindPreviewAndImageCapture(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                // 處理錯誤
                Toast.makeText(requireContext(), "啟動相機時出錯: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("VideoFragment", "啟動相機時出錯", e);
            }
        }, ContextCompat.getMainExecutor(requireContext()));
    }

    // 綁定預覽和拍照的方法
    private void bindPreviewAndImageCapture(@NonNull ProcessCameraProvider cameraProvider) {
        // 配置預覽
        Preview preview = new Preview.Builder().build();
        // 配置拍照
        imageCapture = new ImageCapture.Builder().build();

        // 選擇後置相機
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        // 將預覽設置到 PreviewView
        preview.setSurfaceProvider(binding.previewView.getSurfaceProvider());

        // 綁定相機
        camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);
    }

    // 初始化縮放手勢檢測器
    @SuppressLint("ClickableViewAccessibility")
    private void initializeZoom() {
        ScaleGestureDetector scaleGestureDetector = new ScaleGestureDetector(requireContext(),
                new ScaleGestureDetector.SimpleOnScaleGestureListener() {
                    @Override
                    public boolean onScale(@NonNull ScaleGestureDetector detector) {
                        if (camera != null) {
                            float currentZoomRatio = Objects.requireNonNull(camera.getCameraInfo().getZoomState().getValue()).getZoomRatio();
                            float delta = detector.getScaleFactor();
                            camera.getCameraControl().setZoomRatio(currentZoomRatio * delta);
                        }
                        return true;
                    }
                });

        binding.previewView.setOnTouchListener((v, event) -> {
            scaleGestureDetector.onTouchEvent(event);
            return true;
        });
    }

    // 拍照的方法
    private void takePhoto() {
        if (imageCapture == null) {
            return;
        }

        // 創建照片文件
        File photoFile = new File(requireContext().getExternalFilesDir(null), System.currentTimeMillis() + ".jpg");

        // 創建輸出選項
        ImageCapture.OutputFileOptions outputOptions = new ImageCapture.OutputFileOptions.Builder(photoFile).build();

        // 拍照
        imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(requireContext()), new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                // 顯示縮略圖
                updateThumbnail(photoFile);
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                // 處理錯誤
                Toast.makeText(requireContext(), "拍照失敗: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("VideoFragment", "拍照失敗", exception);
            }
        });
    }

    // 更新縮略圖的方法
    private void updateThumbnail(File photoFile) {
        Uri photoUri = Uri.fromFile(photoFile);
        Glide.with(this)
                .load(photoUri)
                .centerCrop()
                .into(binding.thumbnailView);

        binding.thumbnailView.setOnClickListener(v -> {
            // 在這裡處理縮略圖點擊事件，例如打開全屏查看照片
            Toast.makeText(requireContext(), "查看照片: " + photoFile.getName(), Toast.LENGTH_SHORT).show();
        });

    }

    // 處理權限請求結果
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 如果權限被授予，啟動相機
                startCamera();
            } else {
                // 如果權限被拒絕，顯示提示
                Toast.makeText(requireContext(), "相機權限被拒絕", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
