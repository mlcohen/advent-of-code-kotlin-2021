package org.adventofcode

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals

class SolutionTest {
    @Test fun testSolution() {
        val input = listOf(199, 200, 208, 210, 200, 207, 240, 269, 260, 263)
        assertEquals(Solution.solve(input), 7)
    }
}