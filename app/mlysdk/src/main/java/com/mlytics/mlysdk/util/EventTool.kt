package com.mlytics.mlysdk.util

//import Foundation
class EventTool {

    var map: MutableMap<String, MutableList<(Any) -> Unit>> = mutableMapOf()

    fun deinit() {
        this.unregisterAll()
    }

    fun on(event: String, handler: (Any) -> Unit) {
        var list = map[event]
        if (list == null) {
            list = mutableListOf()
            map[event] = list
        }
        list.add { handler }
    }

    fun emit(event: String, data: Any) {
        map[event]?.forEach {
            it(data)
        }
    }

    fun unregisterAll() {
        map.clear()
    }

    fun unregister(event: String) {
        map.remove(event)
    }

}
