package org.adventofcode

import kotlin.math.max
import kotlin.math.min

enum class LineSegmentBias {
    HORIZONTAL,
    VERTICAL,
    DIAGONAL,
}

data class Point(val x: Int, val y: Int)

data class LineSegment(val p1: Point, val p2: Point) {
    constructor(x1: Int, y1: Int, x2: Int, y2: Int) : this(Point(x1, y1), Point(x2, y2))
    val vertical = p1.x == p2.x
    val horizontal = p1.y == p2.y
    val diagonal = !vertical && !horizontal
    val bias = if (vertical) {
        LineSegmentBias.VERTICAL
    } else if (horizontal) {
        LineSegmentBias.HORIZONTAL
    } else {
        LineSegmentBias.DIAGONAL
    }
    val minX = min(p1.x, p2.x)
    val maxX = max(p1.x, p2.x)
    val minY = min(p1.y, p2.y)
    val maxY = max(p1.y, p2.y)
}

fun LineSegment.forEachPoint(fn: (p: Point) -> Unit) {
    when (bias) {
        LineSegmentBias.HORIZONTAL -> {
            for (x in minX..maxX) fn(Point(x, minY))
        }
        LineSegmentBias.VERTICAL -> {
            for (y in minY..maxY) fn(Point(minX, y))
        }
        LineSegmentBias.DIAGONAL -> {
            val rise = p2.x - p1.x
            val run = p2.y - p1.y

            if (rise > 0 && run > 0) {
                for (step in 0..run) {
                    fn(Point(p1.x + step, p1.y + step))
                }
            } else if (rise < 0 && run < 0){
                for (step in 0..-run) {
                    fn(Point(p1.x - step, p1.y - step))
                }
            } else if (run < 0 && rise > 0) {
                for (step in 0..rise) {
                    fn(Point(p1.x + step, p1.y - step))
                }
            } else { // run > 0 && rise < 0
                for (step in 0..run) {
                    fn(Point(p1.x - step, p1.y + step))
                }
            }
        }
    }
}

fun LineSegment.toPoints(): List<Point> {
    val points = mutableListOf<Point>()
    this.forEachPoint { points.add(it) }
    return points
}