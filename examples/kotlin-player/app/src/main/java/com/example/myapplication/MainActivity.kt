package com.example.myapplication

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.AppCompatButton
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.mlytics.exoplayer.MLYExoPlayer
import com.mlytics.mlysdk.driver.MLYDriver

class MainActivity : AppCompatActivity() {
    private var playerView: StyledPlayerView? = null
    private var playButton: AppCompatButton? = null

    override fun onDestroy() {
        super.onDestroy()
        MLYDriver.deactivate()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        playerView = findViewById<StyledPlayerView>(R.id.player_view)

        MLYDriver.initialize(this.applicationContext) { options ->
            options.client.id = "cegh8d9j11u91ba1u600"
        }

        var player = MLYExoPlayer.build(playerView!!)
        playerView?.player = player

        val videoUrlString = "https://vsp-stream.s3.ap-northeast-1.amazonaws.com/HLS/raw/SpaceX.m3u8"

        val subtitleUrlString = "https://static.gordon.sh/p2sp-test/subtitle_zhtw.vtt"
        val subtitleConfiguration = MediaItem.SubtitleConfiguration.Builder(Uri.parse(subtitleUrlString))
            .setMimeType("text/vtt")
            .setLanguage("zh-TW")
            .setSelectionFlags(C.SELECTION_FLAG_DEFAULT)
            .build()

        val mediaItem = MediaItem.Builder()
            .setUri(Uri.parse(videoUrlString))
            .setSubtitleConfigurations(listOf(subtitleConfiguration))
            .build()

        player.setMediaItem(mediaItem)

        playButton = findViewById(R.id.playButton)
        playButton?.setOnClickListener {
            player.playWhenReady = true
            player.prepare()
            player.play()
        }
    }
}
