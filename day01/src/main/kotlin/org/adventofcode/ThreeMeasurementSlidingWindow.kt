package org.adventofcode

class ThreeMeasurementSlidingWindow {
    companion object {
        fun count(input: List<Int>): Int {
            if (input.size < 3) return 0
            val limit = input.size - 2
            val sums = IntArray(limit) { 0 }
            for (i in 0 until limit) {
                sums[i] = input[i] + input[i + 1] + input[i + 2]
            }
            return DepthMeasurementIncreases.count(sums.toList())
        }
    }
}