package org.adventofcode

import java.io.File

val LINE_SEGMENT_STATEMENT_REGEX = """(\d+),(\d+)\s+->\s+(\d+),(\d+)""".toRegex()

fun parseLineSegmentExpression(input: String): LineSegment {
    val matchResult = LINE_SEGMENT_STATEMENT_REGEX.find(input)
    val (x1, y1, x2, y2) = matchResult!!.destructured
    return LineSegment(x1.toInt(), y1.toInt(), x2.toInt(), y2.toInt())
}

fun parseLineSegmentExpressions(input: List<String>): List<LineSegment> {
    return input.map { parseLineSegmentExpression(it) }
}

fun runPart1Solution(lineSegments: List<LineSegment>) {
    println("Day 5, Part 1 Solution")

    val grid = Grid()

    grid.apply(lineSegments) { !it.diagonal }
//    grid.prettyPrint()

    val result = grid.filterActiveCells { cell -> cell.data.counter > 1 }

    println("result ${result.size}")
}

fun runPart2Solution(lineSegments: List<LineSegment>) {
    println("Day 5, Part 2 Solution")

    val grid = Grid()

    grid.apply(lineSegments)
//    grid.prettyPrint()

    val result = grid.filterActiveCells { cell -> cell.data.counter > 1 }

    println("result ${result.size}")
}

fun main() {
    val rawInputLines = File("day05/src/main/resources/sampleInput.txt").readLines()
    val lineSegments = parseLineSegmentExpressions(rawInputLines)

    runPart1Solution(lineSegments)
    runPart2Solution(lineSegments)
}
