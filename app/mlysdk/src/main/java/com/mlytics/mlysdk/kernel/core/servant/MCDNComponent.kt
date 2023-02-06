package com.mlytics.mlysdk.kernel.core.servant

import com.mlytics.mlysdk.kernel.core.KernelSettings
import com.mlytics.mlysdk.kernel.core.api.base.CDNScoreRequester
import com.mlytics.mlysdk.kernel.core.api.model.CDNScoreRequesterReadPlatformScoresOptions
import com.mlytics.mlysdk.kernel.core.const.service.DomainType
import com.mlytics.mlysdk.kernel.core.const.service.ResourceConstant
import com.mlytics.mlysdk.kernel.core.model.service.Resource
import com.mlytics.mlysdk.util.Component
import java.net.URL

//import Foundation
object MCDNConstant {
    val QUALIFIED_DOWNLOAD_BANDWIDTH = 1.0 * 1024 * 1024
    val MIN_SIZE_FOR_BANDWIDTH_MEASUREMENT = 16 * 1024
    val PENALTY_POWER_OF_AVAILABILITY = 3.0
    val MAX_ACCEPTABLE_SCORE_DIFFERENCE = 0.25
}

typealias  MCDNComponent = MCDNProvider

class MCDNInitializer {

    suspend fun process() {

        this._initializeMCDNStats()
        this._initializeMCDNScores()
        this._forwardCDNConfigRecords()
    }

    suspend fun _initializeMCDNStats() {
        InitializeMCDNStatsHandler().process()
    }

    fun _initializeMCDNScores() {
        InitializeMCDNScoresHandler().process()
    }

    fun _forwardCDNConfigRecords() {

    }

}

class InitializeMCDNStatsHandler {
    suspend fun process() {
        this._intakeConfig()
        this._updateStats()
    }

    suspend fun _intakeConfig() {
        val platforms = KernelSettings.platforms.platforms
        if (platforms != null) else {
            return
        }

        var options = CDNScoreRequesterReadPlatformScoresOptions()

        options.algorithmID = KernelSettings.platforms.algorithm_id
        options.platformIDs = platforms.mapNotNull { it.id }

        val res = CDNScoreRequester().readPlatformScores(options)
        var map: MutableMap<String, Double?> = mutableMapOf()

        platforms.forEach {
            map[it.id ?: ""] = it.score
        }

        KernelSettings.platforms.platforms?.forEach {
            it.score = map[it.id ?: ""] ?: it.score
        }

    }

    fun _updateStats() {
        val platforms = KernelSettings.platforms.platforms
        if (platforms != null) else {
            return
        }

        for (platform in platforms) {
            val cdn = CDN()
            cdn.id = platform.id
            cdn.name = platform.name
            cdn.domain = platform.host
            cdn.type = DomainType.CDN.rawValue
            cdn.isEnabled = platform.enable
            cdn.businessScore = platform.score
            val id = platform.id
            if (id != null) {
                MCDNStatsHolder.cdns[id] = cdn
            }

        }

        MCDNStatsHolder.algorithmID = KernelSettings.platforms.algorithm_id
        MCDNStatsHolder.algorithmVersion = KernelSettings.platforms.algorithm_ver
    }

}

class InitializeMCDNScoresHandler {
    fun process() {
        this._intakeConfig()
        this._updateStats()
    }

    fun _intakeConfig() {

    }

    fun _updateStats() {
        val platforms = KernelSettings.platforms.platforms
        if (platforms != null) else {
            return
        }


        for (platform in platforms) {
            val id = platform.id
            if (id != null) {
                MCDNStatsHolder.cdns[id]?.currentScore = platform.score ?: 1.0
            }

        }

    }

}

object MCDNProvider : Component() {
    override suspend fun activate() {
        this._loadMCDNStats()
        this._initializeMCDN()
    }

    fun _loadMCDNStats() {

    }

    suspend fun _initializeMCDN() {
        MCDNInitializer().process()
    }

    override suspend fun deactivate() {
        this._unloadMCDNStats()
        this._purgeMCDNMetrics()
    }

    fun _unloadMCDNStats() {
        MCDNStats.reset()
    }

    fun _purgeMCDNMetrics() {

    }

}

class MCDNSelector {
    var _content: MCDNSelectorContent? = null
    var _outcome: MutableList<CDN>? = null
    var _isUrgentResource: Boolean = false
        get() {
            val priority = _content?.resource?.priority
            if (priority != null) {
                return priority < ResourceConstant.URGENT_THRESHOLD_PRIORITY
            }

            return false
        }

    fun process() {
        this._intakeResult()
        this._injectOrigin()
        this._intakeOutcome()
    }

    fun _intakeResult() {
        var result = if (this._isUrgentResource) {
            CDNsBasedOnHighScoreGroupHandler().process()
        } else {
            CDNsBasedOnOverallScoresHandler().process()
        }
        _outcome = result
    }

    fun _injectOrigin() {
        var result = this._outcome
        if (result != null) else {
            return
        }

        val resource = this._content!!.resource!!
        val url = URL(resource.uri)
        if (resource != null && url != null) else {
            return
        }

        val originHost = url.host

        for (source in result) {
            if (source.domain == originHost) {
                return
            }

        }

        val cdn = CDN()
        cdn.type = DomainType.ORIGIN.rawValue
        cdn.domain = originHost
        result.add(cdn)
        this._outcome = result
    }

    fun _intakeOutcome() {

    }

    companion object {
        fun process(options: MCDNSelectorOptions): MCDNSelector {
            val sel = MCDNSelector()
            sel._content = options
            sel.process()
            return sel
        }

        fun process(resource: Resource): MCDNSelector {
            val options = MCDNSelectorOptions()
            options.resource = resource
            return this.process(options)
        }

    }

}

class CDNsBasedOnOverallScoresHandler : AbstractCDNsGroupHandler() {
    override fun score(cdn: CDN): Double {
        return cdn.overallScore ?: 0.0
    }

}

class CDNsBasedOnHighScoreGroupHandler : AbstractCDNsGroupHandler() {
    override fun score(cdn: CDN): Double {
        return cdn.currentScore
    }
}

abstract class AbstractCDNsGroupHandler {
    var _primeCDNs: MutableList<CDN>? = null
    var _otherCDNs: MutableList<CDN>? = null
    var _outcome: MutableList<CDN>? = null
    var threshold: Double? = null

    abstract fun score(cdn: CDN): Double

    fun process(): MutableList<CDN> {
        this._intakeThreshold()
        this._intakeCDNs()
        this._intakeResult()
        this._injectOthers()
        this._intakeOutcome()
        return this._outcome!!
    }

    fun _intakeThreshold() {
        var maxScore: Double = 0.0
        val cdns = MCDNStatsHolder.cdns.values

        for (cdn in cdns) {
            if (this.score(cdn) > maxScore) {
                maxScore = this.score(cdn)
            }

        }

        this.threshold = maxScore - MCDNConstant.MAX_ACCEPTABLE_SCORE_DIFFERENCE
    }

    fun _intakeCDNs() {
        val cdns = MCDNStatsHolder.cdns.values
        val minAcceptableScore = this.threshold ?: 0.0
        this._primeCDNs = mutableListOf()
        this._otherCDNs = mutableListOf()

        for (cdn in cdns) {
            if (cdn.isEnabled != true) {
                continue
            }
            if (cdn.currentScore >= minAcceptableScore) {
                this._primeCDNs!!.add(cdn)
            } else {
                this._otherCDNs!!.add(cdn)
            }
        }

    }

    fun _intakeResult() {
        this._outcome = RandomlySelectCDNsHandler().process(
            _primeCDNs!!
        )
    }

    fun _injectOthers(): List<CDN> {

        var result = _outcome
        if (result == null) {
            return mutableListOf<CDN>()
        }

        val others = RandomlySelectCDNsHandler().process(
            _otherCDNs!!
        )

        result.addAll(others)
        return result
    }

    fun _intakeOutcome() {

    }

    companion object {

        fun process(): MutableList<CDN> {
            return CDNsBasedOnHighScoreGroupHandler().process()
        }
    }

}

class RandomlySelectCDNsHandlerContent {
    var cdns: MutableList<CDN>? = null
    var cdnScoreGetter: ((CDN) -> Double?)? = null
}

class RandomlySelectCDNsHandler {

    fun process(cdns: MutableList<CDN>): MutableList<CDN> {
        return cdns.shuffled().toMutableList()
    }

}

class MCDNSelectorContent {
    var resource: Resource? = null
}

typealias MCDNSelectorOptions = MCDNSelectorContent

data class CDN(
    var id: String? = null,
    var name: String? = null,
    var isEnabled: Boolean? = null,
    var meanBandwidth: Double = 0.0,
    var meanAvailability: Double = 1.0,
    var overallScore: Double? = null,
    var currentScore: Double = 1.0,
    var businessScore: Double? = null,
    var type: String? = null,
    var domain: String? = null
)

object MCDNStatsHolder {
    var instance: MCDNStats? = null
    var cdns: MutableMap<String, CDN> = mutableMapOf()
    var algorithmID: String?
        get() {
            return this.algorithmID
        }
        set(newValue) {
            this.algorithmID = newValue
        }

    var algorithmVersion: String?
        set(newValue) {
            this.algorithmVersion = newValue
        }
        get() {
            return algorithmVersion
        }

    var networkBandwidth: Double
        get() {
            return networkBandwidth
        }
        set(newValue) {
            networkBandwidth = newValue
        }

}

typealias  CDNSource = CDN

object MCDNStats {
    var cdns: MutableMap<String, CDN>? = null
    var algorithmID: String? = null
    var algorithmVersion: String? = null
    var networkBandwidth: Double = MCDNConstant.QUALIFIED_DOWNLOAD_BANDWIDTH
    fun reset() {
        this.cdns = null
        this.algorithmID = null
        this.algorithmVersion = null
        this.networkBandwidth = MCDNConstant.QUALIFIED_DOWNLOAD_BANDWIDTH
    }

}
