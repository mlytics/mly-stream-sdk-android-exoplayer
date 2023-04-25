package com.mlytics.mlysdk.demo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.os.Bundle;
import android.view.View;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.mlytics.mlysdk.driver.MLYDriver;
import com.mlytics.mlysdk.driver.MLYDriverOptions;
import com.mlytics.mlysdk.driver.pheripheral.player.MLYExoPlayer;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class MainActivity extends AppCompatActivity {
    private StyledPlayerView playerView = null;
    private AppCompatButton playButton = null;

    String clientId = "cegh8d9j11u91ba1u600";
    String url = "https://vsp-stream.s3.ap-northeast-1.amazonaws.com/HLS/raw/SpaceX.m3u8";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playerView = findViewById(R.id.player_view);

        MLYDriver.INSTANCE.initialize(new Function1<MLYDriverOptions, Unit>() {
            @Override
            public Unit invoke(MLYDriverOptions options) {
                options.getClient().setId(clientId);
                return null;
            }
        });

        ExoPlayer.Builder builder = MLYExoPlayer.INSTANCE.builder(playerView);

        ExoPlayer player = builder.build();
        playerView.setPlayer(player);

        player.setMediaItem(MediaItem.fromUri(url));

        playButton = findViewById(R.id.playButton);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.setPlayWhenReady(true);
                player.prepare();
                player.play();
            }
        });
    }
}