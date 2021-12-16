package org.adventofcode

import java.io.File

typealias HeightMap = List<List<Int>>
data class Location(val row: Int, val col: Int)
data class Entry(val height: Int, val location: Location)

fun HeightMap.heightAtLocation(rowPos: Int, colPos: Int): Int {
    if (rowPos < 0 || rowPos >= this.size) return -1
    val row = this[rowPos]
    if (colPos < 0 || colPos >= row.size) return -1
    return row[colPos]
}

fun HeightMap.findLowestHeights(): List<Entry> {
    var lowestHeightData = mutableMapOf<Location, Int>()
    this.forEachIndexed { rowIdx, row ->
        row.forEachIndexed { colIdx, height ->
            val focalHeight = this.heightAtLocation(rowIdx, colIdx)
            val topHeight = this.heightAtLocation(rowIdx - 1, colIdx)
            val rightHeight = this.heightAtLocation(rowIdx, colIdx + 1)
            val bottomHeight = this.heightAtLocation(rowIdx + 1, colIdx)
            val leftHeight = this.heightAtLocation(rowIdx, colIdx - 1)
            val heights = listOf(focalHeight, topHeight, rightHeight, bottomHeight, leftHeight)
            val heightCount = heights
                .filter { it >= 0 }
                .groupBy { it }
                .mapValues { it.value.size }
            val minHeight = heightCount.keys.minOrNull()!!
            if (focalHeight == minHeight && heightCount[minHeight] == 1 ) {
                lowestHeightData[Location(rowIdx, colIdx)] = focalHeight
            }
        }
    }
    return lowestHeightData.map { (key, value) -> Entry(value, key) }
}

object HeightMapFactory {
    fun fromFile(pathname: String): HeightMap {
        return File(pathname).readLines().map { it.toCharArray().map(Char::digitToInt) }
    }
}

fun main() {
    val heightmap = HeightMapFactory.fromFile("day09/src/main/resources/puzzleInput.txt")
    val lowestHeights = heightmap.findLowestHeights()
    val riskLevel = lowestHeights.fold(0) { sum, (height) -> sum + height + 1 }

    lowestHeights.forEach { (height, loc) -> println("$height @ (${loc.row}, ${loc.col})") }
    println("risk level = $riskLevel")
}
