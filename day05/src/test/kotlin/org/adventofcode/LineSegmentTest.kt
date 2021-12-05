package org.adventofcode

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals

class LineSegmentTest {

    @Test
    fun testLineSegment() {
        val seg1 = LineSegment(1, 3, 3, 3) // horizonal
        val seg2 = LineSegment(5, 1, 5, 3) // vertical
        val seg3 = LineSegment(1, 1, 3, 3) // diagonal +rise +run
        val seg4 = LineSegment(3, 3, 1, 1) // diagonal -rise -run
        val seg5 = LineSegment(1, 3, 3, 1) // diagonal -rise +run
        val seg6 = LineSegment(3, 1, 1, 3) // diagonal +rise -run

        assertEquals(seg1.p1, Point(1, 3))
        assertEquals(seg1.p2, Point(3, 3))
        assertEquals(seg1.bias, LineSegmentBias.HORIZONTAL)
        assertEquals(seg1.horizontal, true)
        assertEquals(seg1.vertical, false)
        assertEquals(seg1.diagonal, false)
        assertEquals(seg1.minX, 1)
        assertEquals(seg1.maxX, 3)
        assertEquals(seg1.minY, 3)
        assertEquals(seg1.maxY, 3)
        assertEquals(seg1.toPoints(), listOf(Point(1, 3), Point(2, 3), Point(3, 3)))

        assertEquals(seg2.p1, Point(5, 1))
        assertEquals(seg2.p2, Point(5, 3))
        assertEquals(seg2.bias, LineSegmentBias.VERTICAL)
        assertEquals(seg2.horizontal, false)
        assertEquals(seg2.vertical, true)
        assertEquals(seg2.diagonal, false)
        assertEquals(seg2.minX, 5)
        assertEquals(seg2.maxX, 5)
        assertEquals(seg2.minY, 1)
        assertEquals(seg2.maxY, 3)
        assertEquals(seg2.toPoints(), listOf(Point(5, 1), Point(5, 2), Point(5, 3)))

        assertEquals(seg3.p1, Point(1, 1))
        assertEquals(seg3.p2, Point(3, 3))
        assertEquals(seg3.bias, LineSegmentBias.DIAGONAL)
        assertEquals(seg3.horizontal, false)
        assertEquals(seg3.vertical, false)
        assertEquals(seg3.diagonal, true)
        assertEquals(seg3.minX, 1)
        assertEquals(seg3.maxX, 3)
        assertEquals(seg3.minY, 1)
        assertEquals(seg3.maxY, 3)
        assertEquals(seg3.toPoints(), listOf(Point(1, 1), Point(2, 2), Point(3, 3)))

        assertEquals(seg4.p1, Point(3, 3))
        assertEquals(seg4.p2, Point(1, 1))
        assertEquals(seg4.bias, LineSegmentBias.DIAGONAL)
        assertEquals(seg4.horizontal, false)
        assertEquals(seg4.vertical, false)
        assertEquals(seg4.diagonal, true)
        assertEquals(seg4.minX, 1)
        assertEquals(seg4.maxX, 3)
        assertEquals(seg4.minY, 1)
        assertEquals(seg4.maxY, 3)
        assertEquals(seg4.toPoints(), listOf(Point(3, 3), Point(2, 2), Point(1, 1)))

        assertEquals(seg5.p1, Point(1, 3))
        assertEquals(seg5.p2, Point(3, 1))
        assertEquals(seg5.bias, LineSegmentBias.DIAGONAL)
        assertEquals(seg5.horizontal, false)
        assertEquals(seg5.vertical, false)
        assertEquals(seg5.diagonal, true)
        assertEquals(seg5.minX, 1)
        assertEquals(seg5.maxX, 3)
        assertEquals(seg5.minY, 1)
        assertEquals(seg5.maxY, 3)
        assertEquals(seg5.toPoints(), listOf(Point(1, 3), Point(2, 2), Point(3, 1)))

        assertEquals(seg6.p1, Point(3, 1))
        assertEquals(seg6.p2, Point(1, 3))
        assertEquals(seg6.bias, LineSegmentBias.DIAGONAL)
        assertEquals(seg6.horizontal, false)
        assertEquals(seg6.vertical, false)
        assertEquals(seg6.diagonal, true)
        assertEquals(seg6.minX, 1)
        assertEquals(seg6.maxX, 3)
        assertEquals(seg6.minY, 1)
        assertEquals(seg6.maxY, 3)
        assertEquals(seg6.toPoints(), listOf(Point(3, 1), Point(2, 2), Point(1, 3)))
    }

}