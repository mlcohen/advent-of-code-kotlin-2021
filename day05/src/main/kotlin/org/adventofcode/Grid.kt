package org.adventofcode

import kotlin.math.max

data class GridCell(val point: Point, val data: GridCellData)

data class GridCellData(val counter: Int = 0)

fun GridCellData.increment(): GridCellData {
    return this.copy(counter = counter + 1)
}

class Grid {
    val cells: MutableMap<Point, GridCellData> = mutableMapOf()
    private var _maxPoint = Point(0, 0)

    val maxPoint: Point
        get() = _maxPoint.copy()

    fun apply(lineSegment: LineSegment) {
        lineSegment.forEachPoint { p ->
            val cell = cells[p] ?: GridCellData()
            cells[p] = cell.increment()
        }
        val maxX = max(lineSegment.maxX, maxPoint.x)
        val maxY = max(lineSegment.maxY, maxPoint.y)
        _maxPoint = Point(maxX, maxY)
    }

    fun apply(
        lineSegments: List<LineSegment>,
        shouldApplyLineSegment: ((segment: LineSegment) -> Boolean)? = null,
    ) {
        lineSegments.forEach { segment ->
            if (shouldApplyLineSegment != null) {
                if (shouldApplyLineSegment(segment)) {
                    apply(segment)
                }
            } else {
                apply(segment)
            }
        }
    }

    fun filterActiveCells(fn: (GridCell) -> Boolean): List<GridCell> {
        val filteredActiveCells = mutableListOf<GridCell>()
        cells.forEach { (point, data) ->
            val cell = GridCell(point, data)
            val result = fn(GridCell(point, data))
            if (result) {
                filteredActiveCells.add(cell)
            }
        }
        return filteredActiveCells
    }

    fun prettyPrint() {
        val header = (0..(_maxPoint.x + 1))
            .joinToString(" ") { if (it == 0) " " else (it - 1).toString() }

        println(header)

        for (y in 0.._maxPoint.y) {
            val line = mutableListOf<String>()
            line.add(y.toString())
            for (x in 0.._maxPoint.x) {
                val p = Point(x, y)
                val cell = cells[p]
                line.add(cell?.counter?.toString() ?: ".")
            }
            println(line.joinToString(" "))
        }
    }
}