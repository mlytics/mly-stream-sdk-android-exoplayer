package com.mlytics.example.mlysdk

import android.media.MediaPlayer
import android.view.SurfaceHolder

class SurfaceCallback(private val mediaPlayer: MediaPlayer?): SurfaceHolder.Callback {

    override fun surfaceCreated(holder: SurfaceHolder) {
        mediaPlayer?.setDisplay(holder)
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
    }
}