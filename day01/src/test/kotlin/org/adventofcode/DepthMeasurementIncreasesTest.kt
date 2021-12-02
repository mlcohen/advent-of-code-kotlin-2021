package org.adventofcode

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals

class DepthMeasurementIncreasesTest {
    @Test fun testDepthMeasurementIncreases() {
        val input = listOf(199, 200, 208, 210, 200, 207, 240, 269, 260, 263)
        assertEquals(DepthMeasurementIncreases.count(input), 7)
    }
}