package com.mlytics.mlysdk.kernel.core.api

import com.mlytics.mlysdk.kernel.core.KernelSettings
import com.mlytics.mlysdk.kernel.core.api.base.MeteringRequester
import com.mlytics.mlysdk.kernel.core.api.model.MeteringAPICreateCDNDownloadMeteringContent
import com.mlytics.mlysdk.kernel.core.api.model.MeteringAPICreateCDNDownloadMeteringData
import com.mlytics.mlysdk.kernel.core.api.model.MeteringAPICreateCDNDownloadMeteringDataItem
import com.mlytics.mlysdk.kernel.core.api.model.MeteringAPICreateP2PDownloadMeteringContent
import com.mlytics.mlysdk.kernel.core.api.model.MeteringAPICreateP2PDownloadMeteringOptions
import com.mlytics.mlysdk.kernel.core.api.model.MeteringAPICreateP2PDownloadMeteringOptionsItem
import com.mlytics.mlysdk.kernel.core.const.PeerType
import com.mlytics.mlysdk.kernel.core.const.TransferType
import com.mlytics.mlysdk.kernel.core.const.base.FlowKey
import com.mlytics.mlysdk.util.AbstractFlow

class MeteringAPICreateCDNDownloadMeteringHandler(content: MeteringAPICreateCDNDownloadMeteringContent) :
    AbstractFlow<MeteringAPICreateCDNDownloadMeteringContent>(content) {
    override suspend fun process() {
        this._intakeData()
        this._forwardData()
    }

    suspend fun _intakeData() {
        val records = this._content.records
        val data = MeteringAPICreateCDNDownloadMeteringData(data = records.map { record ->
            MeteringAPICreateCDNDownloadMeteringDataItem(
                time = record.startTime,
                streamID = KernelSettings.stream.streamID,
                clientID = KernelSettings.client.id,
                sessionID = KernelSettings.client.sessionID,
                ok = record.isSuccess,
                error = record.errorMessage,
                httpCode = record.responseCode,
                url = record.requestURI,
                masterURL = record.swarmURI,
                sourceURL = record.sourceURI,
                hostname = KernelSettings.client.origin,
                platformID = record.id,
                transferSize = record.contentSize,
                duration = record.elapsedTime,
                isComplete = record.isComplete,
                sampleRate = KernelSettings.report.sampleRate,
                algorithmID = record.algorithmID,
                algorithmVer = record.algorithmVersion
            )
        })
        this._expose(FlowKey.DATA, data)
    }

    suspend fun _forwardData() {
        val data = this._require(FlowKey.DATA) as MeteringAPICreateCDNDownloadMeteringData

        MeteringRequester().createCDNDownloadMetering(data)
    }

}

class MeteringAPICreateP2PDownloadMeteringHandler(content: MeteringAPICreateP2PDownloadMeteringContent) :
    AbstractFlow<MeteringAPICreateP2PDownloadMeteringContent>(content) {
    override suspend fun process() {
        this._intakeData()
        this._forwardData()
    }

    suspend fun _intakeData() {
        val records = this._content.records
        val data = MeteringAPICreateP2PDownloadMeteringOptions(records.map { record ->
            MeteringAPICreateP2PDownloadMeteringOptionsItem(
                time = record.startTime,
                streamID = KernelSettings.stream.streamID,
                clientID = KernelSettings.client.id,
                sessionID = KernelSettings.client.sessionID,
                peerID = KernelSettings.client.peerID,
                peerType = PeerType.USER,
                targetPeerID = record.peerID,
                targetPeerType = PeerType.USER,
                url = record.requestURI,
                masterURL = record.swarmURI,
                sourceURL = record.sourceURI,
                transferType = TransferType.DOWNLOAD,
                transferSize = record.contentSize,
                duration = record.elapsedTime,
                isComplete = record.isComplete,
                sampleRate = KernelSettings.report.sampleRate,
                algorithmID = record.algorithmID,
                algorithmVer = record.algorithmVersion
            )
        })
        this._expose(FlowKey.DATA, data)
    }

    suspend fun _forwardData() {
        val data = this._require(FlowKey.DATA) as MeteringAPICreateP2PDownloadMeteringOptions

        MeteringRequester().createP2PDownloadMetering(data)
    }

}
