package com.mlytics.mlysdk.kernel.core.infra.metrics.state

import com.mlytics.mlysdk.kernel.core.const.service.DomainType
import com.mlytics.mlysdk.kernel.core.infra.metrics.model.CDNMetrics
import com.mlytics.mlysdk.kernel.core.infra.metrics.model.CDNMetricsDownload
import com.mlytics.mlysdk.kernel.core.infra.metrics.model.NodeMetrics
import com.mlytics.mlysdk.kernel.core.infra.metrics.model.OriginMetrics
import com.mlytics.mlysdk.kernel.core.infra.metrics.model.SourceMetrics
import com.mlytics.mlysdk.kernel.core.infra.metrics.model.SwarmMetrics
import com.mlytics.mlysdk.kernel.core.infra.metrics.model.TimeSeriesData
import com.mlytics.mlysdk.kernel.core.infra.metrics.model.TrackerMetrics
import com.mlytics.mlysdk.kernel.core.infra.metrics.model.UserMetrics
import com.mlytics.mlysdk.kernel.core.infra.model.CDNConfigRecord
import com.mlytics.mlysdk.kernel.core.infra.model.SwarmStateRecord
import com.mlytics.mlysdk.kernel.core.infra.model.UserStateRecord
import com.mlytics.mlysdk.util.ObjectLike

class MetricsStats {
    var _cdns: ObjectLike<CDNMetrics> = ObjectLike()
    var _origin: OriginMetrics = OriginMetrics()
    var _tracker: TrackerMetrics = TrackerMetrics()
    var _node: NodeMetrics = NodeMetrics()
    var _swarms: ObjectLike<SwarmMetrics> = ObjectLike()
    var _source: SourceMetrics = SourceMetrics()

    init {
        this.reset()
    }

    fun cdns(): ObjectLike<CDNMetrics> {
        return this._cdns
    }

    fun origin(): OriginMetrics {
        return this._origin
    }

    fun tracker(): TrackerMetrics {
        return this._tracker
    }

    fun node(): NodeMetrics {
        return this._node
    }

    fun swarms(): ObjectLike<SwarmMetrics> {
        return this._swarms
    }

    fun source(): SourceMetrics {
        return this._source
    }

    fun reset() {
        this._resetMCDN()
        this._resetP2SP()
        this._resetSource()
    }

    fun resetMCDN() {
        this._resetMCDN()
    }

    fun _resetMCDN() {
        this._setupCDNs()
        this._setupOrigin()
    }

    fun _resetP2SP() {
        this._setupTracker()
        this._setupNode()
        this._setupSwarms()
    }

    fun _resetSource() {
        this._setupSource()
    }

    fun setupCDN(record: CDNConfigRecord) {
        this._setupCDN(record)
    }

    fun setupSwarm(record: SwarmStateRecord) {
        this._setupSwarm(record)
    }

    fun setupUser(record: UserStateRecord) {
        this._setupUser(record)
    }

    fun _setupCDNs() {
        this._cdns = ObjectLike()
    }

    fun _setupCDN(record: CDNConfigRecord) {
        val value = CDNMetrics()
        this._cdns[record.id!!] = value
        value.id = record.id
        value.name = record.name
        value.type = record.type
        value.domain = record.domain
        value.isEnabled = record.isEnabled
        val download = CDNMetricsDownload()
        value.download = download
        download.meanBandwidth.dataset.add(TimeSeriesData(record.meanBandwidth))
        download.meanAvailability.dataset.add(TimeSeriesData(record.meanAvailability))
        download.currentScore.dataset.add(TimeSeriesData(record.currentScore))
    }

    fun _setupOrigin() {
        val value = OriginMetrics()
        value.type = DomainType.ORIGIN.rawValue
        this._origin = value
    }

    fun _setupTracker() {
        this._tracker = TrackerMetrics()
    }

    fun _setupNode() {
        this._node = NodeMetrics()
    }

    fun _setupSwarms() {
        this._swarms = ObjectLike()
    }

    fun _setupSwarm(record: SwarmStateRecord) {
        this._swarms[record.swarmID!!] =
            SwarmMetrics(swarmID = record.swarmID, isAvailable = record.isAvailable)
    }

    fun _setupUser(record: UserStateRecord) {
        this._swarms[record.swarmID!!]!!.users[record.peerID!!] =
            UserMetrics(peerID = record.peerID, isAvailable = record.isAvailable)
    }

    fun _setupSource() {
        this._source = SourceMetrics()
    }

}

object MetricsStatsHolder {

    var instance: MetricsStats? = null

    var cdns: ObjectLike<CDNMetrics>
        get() {
            return instance!!.cdns()
        }
        set(value) {

        }

    var origin: OriginMetrics
        get() {
            return instance!!.origin()
        }
        set(value) {

        }

    var tracker: TrackerMetrics
        get() {
            return instance!!.tracker()
        }
        set(value) {

        }

    var node: NodeMetrics
        get() {
            return instance!!.node()
        }
        set(value) {

        }

    var swarms: ObjectLike<SwarmMetrics>
        get() {
            return instance!!.swarms()
        }
        set(value) {

        }

    var source: SourceMetrics
        get() {
            return instance!!.source()
        }
        set(value) {

        }

    fun resetMCDN() {
        instance!!.resetMCDN()
    }

    fun setupCDN(record: CDNConfigRecord) {
        instance!!.setupCDN(record)
    }

    fun setupSwarm(record: SwarmStateRecord) {
        instance!!.setupSwarm(record)
    }

    fun setupUser(record: UserStateRecord) {
        instance!!.setupUser(record)
    }

}
