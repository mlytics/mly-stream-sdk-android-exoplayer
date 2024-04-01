package com.example.myapplication

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.AppCompatButton
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.mlytics.exoplayer.MLYExoPlayer
import com.mlytics.mlysdk.driver.MLYDriver

class MainActivity : AppCompatActivity() {
    private val clientId = "cegh8d9j11u91ba1u600"
    private val videoUrlString = "https://vsp-stream.s3.ap-northeast-1.amazonaws.com/HLS/raw/SpaceX.m3u8"
    private val subtitleUrlString = "https://static.gordon.sh/p2sp-test/subtitle_zhtw.vtt"
    private var player: ExoPlayer? = null
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
            options.client.id = clientId
        }

        MLYExoPlayer.latency = 13000
        this.player = MLYExoPlayer.build(playerView!!)
        player?.playWhenReady = true

        playerView?.player = player
        playerView?.controllerShowTimeoutMs = -1

        this.playVideo()

        playButton = findViewById(R.id.playButton)
        playButton?.setOnClickListener {
            playVideo()
        }
    }

    private fun playVideo() {
        val videoUri = Uri.parse(videoUrlString)

        val subtitleUri = Uri.parse(subtitleUrlString)
        val subtitleConfiguration = MediaItem.SubtitleConfiguration.Builder(subtitleUri)
            .setMimeType("text/vtt")
            .setLanguage("zh-TW")
            .setSelectionFlags(C.SELECTION_FLAG_DEFAULT)
            .build()

        val mediaItem = MediaItem.Builder()
            .setUri(videoUri)
            .setSubtitleConfigurations(listOf(subtitleConfiguration))
            .build()

        player?.setMediaItem(mediaItem)
        player?.prepare()
        player?.play()
    }
}
