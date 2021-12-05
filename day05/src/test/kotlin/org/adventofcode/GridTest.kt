package org.adventofcode

import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals

class GridTest {

    @Test fun testGridHorizontalVerticalOnly() {
        val lineSegments = listOf(
            LineSegment(3, 0, 4, 0),
            LineSegment(4, 3, 4, 4),
            LineSegment(0, 2, 3, 2),
            LineSegment(1, 1, 1, 4),
            LineSegment(0, 4, 2, 4),
        )
        val grid = Grid()
        grid.apply(lineSegments) { !it.diagonal }
        val cells = grid.filterActiveCells { cell -> cell.data.counter > 1 }
        assertEquals(cells, listOf(
            GridCell(point=Point(x=1, y=2), data=GridCellData(counter=2)),
            GridCell(point=Point(x=1, y=4), data=GridCellData(counter=2)),
        ))
    }

    @Test fun testGridDiagonalOnly() {
        val lineSegments = listOf(
            LineSegment(1, 1, 3, 3),
            LineSegment(1, 3, 3, 1),
        )
        val grid = Grid()
        grid.apply(lineSegments)
        val cells = grid.filterActiveCells { cell -> cell.data.counter > 1 }
        assertEquals(cells, listOf(
            GridCell(point=Point(x=2, y=2), data=GridCellData(counter=2)),
        ))
    }
}
