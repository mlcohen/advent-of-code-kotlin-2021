package org.adventofcode

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals

class CoreTest {
    @Test fun testTargetContainsProbe() {
        val targetArea = TargetArea(topLeft = Point(20, -5), bottomRight = Point(30, -10))
        listOf(
            // inside
            Point(20, -5) to true,
            Point(30, -10) to true,
            Point(30, -5) to true,
            Point(20, -10) to true,
            Point(25, -7) to true,

            // outside
            Point(19, -4) to false,
            Point(19, -5) to false,
            Point(31, -10) to false,
            Point(30, -4) to false,
            Point(20, -11) to false,
        ).forEach { (p, expectedResult) ->
            val probe = Probe(position = p)
            assertEquals(probe in targetArea, expectedResult, "Expect $expectedResult for $probe")
        }
    }

    @Test fun testProbeBeforeTarget() {
        val targetArea = TargetArea(topLeft = Point(20, -5), bottomRight = Point(30, -10))
        listOf(
            // before
            Point(19, -4) to true,
            Point(19, -5) to true,
            Point(19, -10) to true,
            Point(20, -4) to true,
            Point(30, -4) to true,

            // inside
            Point(30, -10) to false,
            Point(30, -5) to false,
            Point(20, -10) to false,
            Point(20, -5) to false,
            Point(25, -7) to false,

            // past
            Point(31, -10) to false,
            Point(31, -5) to false,
            Point(20, -11) to false,
            Point(30, -11) to false,
            Point(31, -11) to false,
            Point(19, -11) to false,
            Point(31, -4) to false,
        ).forEach { (p, expectedResult) ->
            val probe = Probe(position = p)
            assertEquals(probe.before(targetArea), expectedResult, "Expect $expectedResult for $probe")
        }
    }

    @Test fun testProbePastTarget() {
        val targetArea = TargetArea(topLeft = Point(20, -5), bottomRight = Point(30, -10))
        listOf(
            // before
            Point(19, -4) to false,
            Point(19, -5) to false,
            Point(19, -10) to false,
            Point(20, -4) to false,
            Point(30, -4) to false,

            // inside
            Point(30, -10) to false,
            Point(30, -5) to false,
            Point(20, -10) to false,
            Point(20, -5) to false,
            Point(25, -7) to false,

            // past
            Point(31, -10) to true,
            Point(31, -5) to true,
            Point(20, -11) to true,
            Point(30, -11) to true,
            Point(31, -11) to true,
            Point(19, -11) to true,
            Point(31, -4) to true,
        ).forEach { (p, expectedResult) ->
            val probe = Probe(position = p)
            assertEquals(probe.past(targetArea), expectedResult, "Expect $expectedResult for $probe")
        }
    }

    @Test fun testStationaryProbeStep() {
        val stationaryProbe = Probe()
        assertEquals(stationaryProbe.step(), Probe(Point(0, 0), Velocity(0, -1)))
    }

    @Test fun testProbeStep() {
        val initProbe = Probe(velocity = Velocity(7, 2))
        val expectedPositions = listOf(
            Point(0, 0),
            Point(7, 2),
            Point(13, 3),
            Point(18, 3),
            Point(22, 2),
            Point(25, 0),
            Point(27, -3),
            Point(28, -7),
            Point(28, -12),
        )
        val result = generateSequence(initProbe) { it.step() }
            .take(expectedPositions.size)
            .toList()

        result.forEach { println(it) }
        result.forEachIndexed { idx, probe ->
            val expectedPosition = expectedPositions[idx]
            assertEquals(probe.position, expectedPosition, "Expect probe $probe to have position $expectedPosition")
        }
    }
}