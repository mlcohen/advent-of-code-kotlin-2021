package org.adventofcode

class DepthMeasurementIncreases {
    companion object {
        fun count (values: List<Int>): Int {
            return values.foldIndexed(0) {  idx, acc, value ->
                when (idx) {
                    0 -> acc
                    else -> if (value > values[idx - 1]) acc + 1 else acc
                }
            }
        }
    }
}