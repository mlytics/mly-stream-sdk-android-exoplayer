package com.mlytics.example.mlysdk

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.SurfaceView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.mlytics.mlysdk.driver.MLYDriver
import com.mlytics.mlysdk.kernel.core.utility.ProxyURLModifier

class MainActivity : AppCompatActivity() {

    private var surfaceView: SurfaceView? = null
    private var playButton: AppCompatButton? = null
    private var mediaPlayer: MediaPlayer? = null
    private var url = "https://vsp-stream.s3.ap-northeast-1.amazonaws.com/HLS/raw/SpaceX.m3u8"

    override fun onDestroy() {
        super.onDestroy()
        MLYDriver.deactivate()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        MLYDriver.initialize("cegh8d9j11u91ba1u600", "Wr7t2lePF6uVvHpi4g0sqcoMkDX89Q5G")

        val uri = ProxyURLModifier.replace(url)

        mediaPlayer = MediaPlayer().apply {
            setDataSource(applicationContext, uri)
            setOnPreparedListener {
                it.start()
                it.isLooping = true
            }
            try {
                prepare()
            } catch (e: Exception) {
                Log.e(localClassName, "player.prepare()", e)
            }
        }

        surfaceView = findViewById(R.id.surfaceView)
        surfaceView?.holder?.addCallback(SurfaceCallback(mediaPlayer))

        playButton = findViewById(R.id.playButton)
        playButton?.setOnClickListener {
            mediaPlayer?.apply {
                if (isPlaying) {
                    pause()
                } else {
                    start()
                }
            }
        }

    }

}
