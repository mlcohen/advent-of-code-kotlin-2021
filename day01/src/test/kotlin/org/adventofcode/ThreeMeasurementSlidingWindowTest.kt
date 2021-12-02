package org.adventofcode

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class ThreeMeasurementSlidingWindowTest {
    @Test fun testThreeMeasurementSlidingWindow() {
        val input = listOf(199, 200, 208, 210, 200, 207, 240, 269, 260, 263)
        Assertions.assertEquals(ThreeMeasurementSlidingWindow.count(input), 5)
    }
}