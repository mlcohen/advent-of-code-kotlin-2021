package org.adventofcode

import java.io.File

typealias HeightMap = List<List<Int>>

data class Point(val row: Int, val col: Int)

data class Basin(val points: Set<Point>)

fun Basin.contains(row: Int, col: Int): Boolean {
    return Point(row, col) in points
}

fun <T> HeightMap.checkPoint(row: Int, col: Int, fn: ((Boolean) -> T)): T {
    val validPoint = if (row < 0 || row >= this.size) {
        false
    } else (col >= 0 && col < this[row].size)
    return fn(validPoint)
}

fun HeightMap.validPoint(row: Int, col: Int): Boolean {
    return checkPoint(row, col) { valid -> valid }
}

fun HeightMap.validPoint(p: Point): Boolean {
    return validPoint(p.row, p.col)
}

fun HeightMap.heightAtOrNull(row: Int, col: Int): Int? {
    return checkPoint(row, col) { valid -> if (valid) this[row][col] else null }
}

fun HeightMap.heightAtOrNull(p: Point): Int? {
    return heightAtOrNull(p.row, p.col)
}

fun HeightMap.neighbouringPointsAt(row: Int, col: Int): List<Point> {
    val top = Point(row -1, col)
    val left = Point(row, col - 1)
    val bottom = Point(row + 1, col)
    val right = Point(row, col + 1)

    return listOf(top, left, bottom, right).filter { validPoint(it) }
}

fun HeightMap.neighbouringPointsAt(p: Point): List<Point> {
    return neighbouringPointsAt(p.row, p.col)
}

fun HeightMap.findLowestPoints(): List<Point> {
    var lowestPoints = mutableListOf<Point>()
    this.forEachIndexed { rowIdx, row ->
        row.forEachIndexed { colIdx, focalHeight ->
            val focalPoint = Point(rowIdx, colIdx)
            val neighbouringPoints = neighbouringPointsAt(rowIdx, colIdx)
            val heightCount = neighbouringPoints
                .map { it to heightAtOrNull(it)!! }
                .plus(focalPoint to focalHeight)
                .groupingBy { it.second }
                .eachCount()
            val minHeight = heightCount.keys.minOrNull()!!
            if (focalHeight == minHeight && heightCount[minHeight] == 1 ) {
                lowestPoints += focalPoint
            }
        }
    }
    return lowestPoints
}

fun HeightMap.neighbouringBasinPointsAt(p: Point): List<Point> {
    return neighbouringPointsAt(p)
        .map { it to heightAtOrNull(it) }
        .filter { when (it.second) {
            9 -> false
            else -> true
        } }
        .map { (p) -> p }
}

fun HeightMap.findAllBasinPointsStartingAt(
    p: Point,
    basinPoints: Set<Point> = setOf(),
): Set<Point> {
    val neighbouringPoints = neighbouringBasinPointsAt(p)
    val nextSearchablePoints = neighbouringPoints.filter { it !in basinPoints }

    if (nextSearchablePoints.isEmpty()) {
        return basinPoints
    }

    val collectedPoints = basinPoints + p + nextSearchablePoints

    return nextSearchablePoints.fold(collectedPoints) { collection, nextPoint ->
        collection + findAllBasinPointsStartingAt(nextPoint, collection)
    }
}

fun HeightMap.basinFrom(p: Point): Basin {
    val entries = findAllBasinPointsStartingAt(p)
    return Basin(entries)
}

fun HeightMap.basinFrom(row: Int, col: Int): Basin {
    return basinFrom(Point(row, col))
}

fun HeightMap.findAllBasins(): List<Basin> {
    return this.findLowestPoints().map { basinFrom(it) }
}

object HeightMapFactory {
    fun fromFile(pathname: String): HeightMap {
        return File(pathname).readLines().map { it.toCharArray().map(Char::digitToInt) }
    }
}

fun HeightMap.prettyPrintWithBasins() {
    val basins = this.findAllBasins()
    val lowestPoints = basins.map { basin -> basin.points
        .map { p -> p to heightAtOrNull(p) }
        .minByOrNull { (_, height) -> height!! }?.first
    }.toSet()

    val basinChars = "12345678".toList()
    this.forEachIndexed { rowIdx, rowData ->
        val points = rowData.mapIndexed { colIdx, height ->
            val matchingBasin = basins
                .mapIndexed { idx, basin -> Pair(idx, basin) }
                .firstOrNull { (_, basin) -> basin.contains(rowIdx, colIdx) }

            if (height == 9) {
                '.'
            } else if (Point(rowIdx, colIdx) in lowestPoints) {
                '@'
            } else matchingBasin?.let {
                val (basinIdx) = matchingBasin
                val charIdx = basinIdx.mod(basinChars.size)
                basinChars[charIdx]
            } ?: '?'
        }
        println(points.joinToString(" "))
    }
}

fun runSolutionPart1(heightmap: HeightMap) {
    println("Day 9 Solution: Part 1")

    val lowestPoints = heightmap.findLowestPoints()
    val riskLevel = lowestPoints.fold(0) { sum, (height) -> sum + height + 1 }

    lowestPoints
        .map { it to heightmap.heightAtOrNull(it) }
        .forEach { (p, height) ->
            println("$height @ (${p.row}, ${p.col})")
        }

    println("risk level = $riskLevel")
}

fun runSolutionPart2(heightmap: HeightMap) {
    println("Day 9 Solution: Part 2")

    val basins = heightmap
        .findAllBasins()
        .sortedByDescending { it.points.size }
        .take(3)

    basins.forEachIndexed { idx, basin ->
        println("${idx + 1}. basin size = ${basin.points.size}")
    }

    val value = basins.fold(1) { total, basin -> total * basin.points.size }

    println("result = $value")
}

fun main() {
    val heightmap = HeightMapFactory.fromFile("day09/src/main/resources/puzzleInput.txt")
//    runSolutionPart1(heightmap)
//    println()
//    runSolutionPart2(heightmap)
    heightmap.prettyPrintWithBasins()
}
