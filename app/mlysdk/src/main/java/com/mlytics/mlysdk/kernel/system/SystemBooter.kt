package com.mlytics.mlysdk.kernel.system

import com.mlytics.mlysdk.util.Component

//import Foundation
object SystemBooter {
    var isActivated = false
    var components: MutableList<Component> = mutableListOf()
    init {
        register()
    }
    suspend fun activate() {
        for (v in this.components) {
            v.activate()
        }
        this.isActivated = true
    }

    suspend fun deactivate() {

        for (v in this.components.reversed()) {
            v.deactivate()
        }

        this.isActivated = false
    }

    fun available(): Boolean {
        if (this.isActivated) else {
            return false
        }


        for (v in this.components) {
            if (v.isActivated) else {
                return false
            }

        }

        return true
    }

    fun register(component: Component) {
        this.components.add(component)
    }

    fun register() {
//        register(ProxyComponent)
        register(SystemComponent)
//        register(MCDNComponent)
//        register(CacheComponment)
//        register(FilerProvider)
//        register(MetricsProvider)
    }

}

typealias  SystemBooterHolder = SystemBooter
