package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.AppCompatButton
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.mlytics.mlysdk.driver.MLYDriver
import com.mlytics.mlysdk.driver.pheripheral.player.MLYExoPlayer

class MainActivity : AppCompatActivity() {
    private var playerView: StyledPlayerView? = null
    private var playButton: AppCompatButton? = null

    val clientId = "cegh8d9j11u91ba1u600"
    val url = "https://vsp-stream.s3.ap-northeast-1.amazonaws.com/HLS/raw/SpaceX.m3u8"

    override fun onDestroy() {
        super.onDestroy()
        MLYDriver.deactivate()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        playerView = findViewById<StyledPlayerView>(R.id.player_view)


        MLYDriver.initialize { options ->
            options.client.id = "cgsangrvdp42j9d4c4v0"
            options.debug = true
        }

        var player = MLYExoPlayer.buildPlayer(playerView!!,2000)

        playerView?.player = player
        player.setMediaItem(MediaItem.fromUri("https://lowlatencydemo.mlytics.co/app/stream/abr.m3u8"))

        playButton = findViewById(R.id.playButton)
        playButton?.setOnClickListener {
            player.playWhenReady = true
            player.prepare()
            player.play()
        }
    }
}
