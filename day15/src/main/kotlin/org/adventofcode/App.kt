package org.adventofcode

import java.io.File

data class Point(val col: Int, val row: Int)
typealias Path = List<Point>

fun Point.above(): Point = Point(col, row - 1)
fun Point.below(): Point = Point(col, row + 1)
fun Point.right(): Point = Point(col + 1, row)
fun Point.left(): Point = Point(col - 1, row)
fun Point.neighbors(): List<Point> = listOf(above(), below(), left(), right())
fun Point.toCompactString(): String = "($col,$row)"

val ORIGIN_POINT = Point(0, 0)

interface Grid {
    val width: Int;
    val height: Int;
    val lastPoint: Point;
    operator fun contains(p: Point): Boolean;
    operator fun get(p: Point): Int?;
}

data class ImmutableGrid(val data: List<List<Int>>) : Grid {
    override val height: Int by lazy { data.size }
    override val width: Int by lazy { if (data.isNotEmpty()) data[0].size else 0 }
    override val lastPoint: Point by lazy { Point(width - 1, height -1 ) }

    override operator fun contains(p: Point): Boolean {
        val insideRowRange = p.row in 0 until height
        val insideColRange = p.col in 0 until width
        return insideRowRange && insideColRange
    }

    override fun get(p: Point): Int? {
        return data[p.row][p.col]
    }
}

fun ImmutableGrid.prettyPrint(withPath: Path? = null) {
    val pointset = withPath?.let { it.toSet() } ?: setOf()
    val padding = 2 + (withPath?.let { 4 } ?: 0)
    this.data.forEachIndexed { rowIdx, row ->
        val s = row.mapIndexed { colIdx, value ->
            val point = Point(colIdx, rowIdx)
            val attr = withPath?.let { if (point in pointset) "{*}" else null }
            "$value${attr?.let { it } ?: ""}".padEnd(padding)
        }
        println(s.joinToString(""))
    }
}

data class TraversalEntry(val parent: Point? = null, val cost: Double = 0.0)

object PathFinder {

    fun findShortestPath(grid: Grid): Path {
        val destinationPoint = grid.lastPoint
        val initTraversalEntry = TraversalEntry()
        val traversedEntries = mutableMapOf(ORIGIN_POINT to initTraversalEntry)
        val openEntries = mutableListOf(ORIGIN_POINT to initTraversalEntry)

        while (openEntries.isNotEmpty()) {
            val (currentPoint, currentEntry) = openEntries.removeFirst()

            if (currentPoint == destinationPoint) {
                break
            }

            val neighboringPoints = currentPoint.neighbors().filter { p -> p in grid }

            for (neighborPoint in neighboringPoints) {
                val neighborValue = grid[neighborPoint] ?: throw error("Invalid neighboring point $neighborPoint")
                val nextCost = currentEntry.cost + neighborValue
                val neighborEntry = traversedEntries[neighborPoint]
                val entry = if ((neighborEntry == null) || (nextCost < neighborEntry.cost)) {
                    TraversalEntry(parent = currentPoint, cost = nextCost)
                } else null

                entry?.let {
                    openEntries.add(neighborPoint to it)
                    traversedEntries[neighborPoint] = it
                }
            }

            openEntries.sortBy { (_, entry) -> entry.cost }
        }

        val destinationEntry = traversedEntries[destinationPoint]
        val pathSequence = generateSequence(destinationEntry) {
            traversedEntries[it.parent]
        }
        return  pathSequence.toList().mapNotNull { it.parent }.reversed() + destinationPoint
    }

}

fun main() {
    val data = File("day15/src/main/resources/puzzleInput.txt")
        .readLines()
        .map { it.split("").drop(1).dropLast(1).map(String::toInt) }

    val grid = ImmutableGrid(data)
    val path = PathFinder.findShortestPath(grid)
    grid.prettyPrint(path)
    println()
    val cost = path.drop(1).sumOf { grid[it] ?: 0 }
    println("total cost: $cost")
}
