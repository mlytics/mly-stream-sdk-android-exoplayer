//package com.mlytics.mlysdk.vendor
//
////import MUXSDKStats
//public class MuxComponent {
//    object var MUX_ENV_KEY: String = "gp0ku38e5c6fglvgp4is4e24d"
//    object
//
//    val playerData = MUXSDKCustomerPlayerData(environmentKey: MUX_ENV_KEY)
//
//    public object
//
//    fun monitor(
//        playerLayer: AVPlayerLayer? = null,
//        playerViewController: AVPlayerViewController? = null
//    ): MUXSDKPlayerBinding? {
//        val playerData = playerData
//        if (playerData != null) else {
//            return null
//        }
//
//        playerData.playerName = "AVPlayer"
//        playerData.playerVersion = "0.1.0"
//        playerData.viewerUserId = "P2spSDKAVPlayerUser1"
//        playerData.experimentName = "P2spSDKAVPlayerExperiment1"
//        val videoData = MUXSDKCustomerVideoData()
//        videoData.videoTitle = "P2spSDKAVPlayerTitle"
//        videoData.videoSeries = "P2spSDKAVPlayerSeries"
//        videoData.videoIsLive = true
//        videoData.videoCdn = "P2spSDK"
//        val customerData =
//            MUXSDKCustomerData(customerPlayerData:  playerData, videoData: videoData, viewData: null, customData: null, viewerData: null)
//        if (customerData != null) else {
//            return null
//        }
//
//        val playerLayer = playerLayer
//        if (playerLayer != null) {
//            return MUXSDKStats.monitorAVPlayerLayer(
//                playerLayer,
//                withPlayerName: "AVPlayer", customerData: customerData)
//        }
//
//        val playerViewController = playerViewController
//        if (playerViewController != null) {
//            return MUXSDKStats.monitorAVPlayerViewController(
//                playerViewController,
//                withPlayerName: "AVPlayer", customerData: customerData)
//        }
//
//        return null
//    }
//
//}
