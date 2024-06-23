package com.example.myapplication.ui.Memory;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

public class VideoFragment extends Fragment {

    private SimpleExoPlayer player;
    private PlayerView playerView;
    private static final String TAG = "VideoFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_video, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        playerView = view.findViewById(R.id.player_view);

        player = new SimpleExoPlayer.Builder(requireContext()).build();
        playerView.setPlayer(player);

        Uri uri = Uri.parse("http://26.136.217.149:5000/video_feed");
        DataSource.Factory dataSourceFactory = new DefaultHttpDataSourceFactory(
                Util.getUserAgent(requireContext(), "MainActivity"));

        ProgressiveMediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(MediaItem.fromUri(uri));

        // 检查 SimpleExoPlayer 对象是否为空
        if (player != null) {
            player.setMediaSource(mediaSource);
            player.prepare();
            player.play();

            // 使用 Handler 定期更新 ProgressiveMediaSource
            Handler handler = new Handler();
            Runnable updateRunnable = new Runnable() {
                @Override
                public void run() {
                    // 重新創建 ProgressiveMediaSource，這樣可以處理新的影片串流
                    ProgressiveMediaSource newSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                            .createMediaSource(MediaItem.fromUri(uri));
                    player.setMediaSource(newSource);
                    player.prepare();
                    handler.postDelayed(this, 10000); // 每 10 秒更新一次
                }
            };
            handler.postDelayed(updateRunnable, 10000); // 第一次延遲 10 秒後執行

            Log.d(TAG, "ExoPlayer 初始化完成並開始播放影片串流");
        } else {
            Log.e(TAG, "SimpleExoPlayer object is null, unable to set initial media source.");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.release();
            player = null;
            Log.d(TAG, "ExoPlayer 已釋放");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (player != null) {
            player.pause();
            Log.d(TAG, "ExoPlayer 已暫停");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (player != null) {
            player.play();
            Log.d(TAG, "ExoPlayer 已恢復播放");
        }
    }
}
