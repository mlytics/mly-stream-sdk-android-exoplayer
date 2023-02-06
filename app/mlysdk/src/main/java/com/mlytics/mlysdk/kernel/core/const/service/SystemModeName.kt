package com.mlytics.mlysdk.kernel.core.const.service

object SystemModeName {
    val MCDN_ONLY = "mcdn_only"
    val P2P_MCDN = "p2p_mcdn"
    val P2P_P2S = "p2p_p2s"
    val P2S_ONLY = "p2s_only"
    val systemP2PModes = listOf<String>(P2P_MCDN, P2P_P2S)
    val systemP2PFallbackModes =
        listOf<List<String>>(listOf(P2P_MCDN, MCDN_ONLY), listOf(P2P_P2S, P2S_ONLY))
}
