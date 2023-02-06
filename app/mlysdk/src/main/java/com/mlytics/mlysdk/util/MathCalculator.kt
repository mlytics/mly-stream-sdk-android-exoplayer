package com.mlytics.mlysdk.util

object MathCalculator {

    fun weightedAverage(data: List<WeightedData>): Double {
        if (data.isEmpty()) {
            return 0.0
        }

        if (data.size == 1) {
            return data[0].value
        }

        var sum = 0.0
        var last = WeightedData()

        for (datum in data) {
            sum += datum.value * (datum.offset + last.offset) * (datum.offset - last.offset)
            last = datum
        }

        val offset = data[data.size - 1].offset
        return sum / (offset * offset)
    }

}

class WeightedData(
    var offset: Double = 0.0,
    var value: Double = 0.0
)
