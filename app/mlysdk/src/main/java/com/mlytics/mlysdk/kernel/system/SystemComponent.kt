package com.mlytics.mlysdk.kernel.system

import com.mlytics.mlysdk.util.Component

object SystemComponent : Component() {
    var initializer: SystemInitializer = SystemInitializer()
    override suspend fun activate() {
        this.initializer.process()
        this.isActivated = true
    }

}
