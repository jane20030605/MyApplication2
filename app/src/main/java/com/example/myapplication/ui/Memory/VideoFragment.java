package com.example.myapplication.ui.Memory;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.databinding.FragmentVideoBinding;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;

public class VideoFragment extends Fragment {
    private static final String TAG = "VideoFragment";
    private FragmentVideoBinding binding;
    private ExoPlayer player;
    private ConnectivityManager.NetworkCallback networkCallback;

    public static VideoFragment newInstance() {
        return new VideoFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: 正在加載佈局");
        binding = FragmentVideoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated: 正在初始化 ViewModel");
        VideoViewModel mViewModel = new ViewModelProvider(this).get(VideoViewModel.class);

        // 初始化 ExoPlayer
        initializePlayer();

        // 監聽網路狀態
        registerNetworkCallback();
    }

    private void initializePlayer() {
        Log.d(TAG, "initializePlayer: 正在初始化 ExoPlayer");
        player = new ExoPlayer.Builder(requireContext()).build();
        PlayerView playerView = binding.videoView;
        playerView.setPlayer(player);

        // 創建自定義的視訊源
        CustomVideoSource videoSource = new CustomVideoSource();
        player.setMediaSource(videoSource.buildMediaSource());
        player.prepare();
        player.play();
        Log.d(TAG, "initializePlayer: 播放器已啟動");
    }


    @SuppressLint("ObsoleteSdkInt")
    private void registerNetworkCallback() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            ConnectivityManager connectivityManager = (ConnectivityManager) requireContext()
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkRequest request = new NetworkRequest.Builder()
                    .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    .build();

            networkCallback = new ConnectivityManager.NetworkCallback() {
                @Override
                public void onLost(@NonNull Network network) {
                    super.onLost(network);
                    Log.d(TAG, "onLost: 網路連接已中斷");
                    // 在此處理網路連接中斷的情況，可以暫停播放器或顯示錯誤訊息
                }

                @Override
                public void onUnavailable() {
                    super.onUnavailable();
                    Log.d(TAG, "onUnavailable: 網路不可用");
                    // 在此處理網路不可用的情況，可以暫停播放器或顯示錯誤訊息
                }

                @Override
                public void onAvailable(@NonNull Network network) {
                    super.onAvailable(network);
                    Log.d(TAG, "onAvailable: 網路已連接");
                    // 在此處理網路重新連接的情況，可以恢復播放器或隱藏錯誤訊息
                }
            };

            connectivityManager.registerNetworkCallback(request, networkCallback);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: 正在釋放播放器");
        if (player != null) {
            player.release();
            player = null;
            Log.d(TAG, "onStop: 播放器已釋放");
        }

        // 解除網路狀態監聽器
        unregisterNetworkCallback();
    }

    @SuppressLint("ObsoleteSdkInt")
    private void unregisterNetworkCallback() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            ConnectivityManager connectivityManager = (ConnectivityManager) requireContext()
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (networkCallback != null) {
                connectivityManager.unregisterNetworkCallback(networkCallback);
                networkCallback = null;
            }
        }
    }
}
