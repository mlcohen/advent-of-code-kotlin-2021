package org.adventofcode

import java.io.File

typealias EnergyLevel = Int;
typealias EnergyGrid = List<List<EnergyLevel>>;
typealias EnergyGridMap = Map<Point, EnergyLevel>
data class Point(val row: Int = 0, val col: Int = 0)
data class Simulation(val grid: EnergyGrid)
data class FlashResult(val energyGridMap: EnergyGridMap, val flashedPoints: Set<Point>)
data class SimulationStepResult(val simulation: Simulation, val flashedPoints: Set<Point>) {
    val grid = simulation.grid
}

fun EnergyLevel.step(): EnergyLevel {
    return when (this) {
        9 -> 0
        else -> this + 1
    }
}

fun Point.toCompactString(): String {
    return "($row, $col)"
}

operator fun EnergyGrid.contains(p: Point): Boolean {
    if (p.row < 0 || p.row >= this.size) return false
    val rowData = this[p.row]
    if (p.col < 0 || p.col >= rowData.size) return false
    return true
}

fun EnergyGrid.findAllPointsNeighboring(p: Point): List<Point> {
    return (-1..1).map { rowOffset ->
        (-1..1).map { colOffset ->
            val neighboringPoint = Point(p.row + rowOffset, p.col + colOffset)
            if (neighboringPoint in this && neighboringPoint != p) {
                neighboringPoint
            } else null
        }
    }.flatten().filterNotNull()
}

fun EnergyGrid.entryCount(): Int {
    val rowCount = this.size
    if (rowCount == 0) return 0
    return rowCount * this[0].size
}

fun EnergyGrid.map(fn: ((Point, EnergyLevel) -> EnergyLevel)? = null): EnergyGrid {
    return this.mapIndexed { rowIdx, rowData ->
        rowData.mapIndexed { colIdx, energyLevel ->
            val point = Point(rowIdx, colIdx)
            fn?.let { fn(point, energyLevel) } ?: energyLevel
        }
    }
}

fun EnergyGrid.toMap(
    fn: ((Point, EnergyLevel) -> EnergyLevel)? = null,
): Map<Point, EnergyLevel> {
    return this.mapIndexed { rowIdx, rowData ->
        rowData.mapIndexed { colIdx, energyLevel ->
            val point = Point(rowIdx, colIdx)
            val nextEnergyLevel = fn?.let { fn(point, energyLevel) } ?: energyLevel
            point to nextEnergyLevel
        }
    }.flatten().toMap()
}

fun EnergyGrid.prettyPrint() {
    this.map { rowData ->
        println(rowData.joinToString(" "))
    }
}

fun EnergyGridMap.applyTo(energyGrid: EnergyGrid): EnergyGrid {
    return energyGrid.map { point, energy -> this[point].let { it } ?: energy }
}

fun Simulation.flashAt(
    focusPoint: Point,
    energyGridMap: EnergyGridMap,
    flashedPoints: Set<Point> = setOf(),
): FlashResult {
    val focusEnergyLevel = energyGridMap[focusPoint]

    if (focusEnergyLevel != 0 || focusPoint in flashedPoints) {
        return FlashResult(energyGridMap, flashedPoints)
    }

    val neighboringPoints = grid.findAllPointsNeighboring(focusPoint)
    val updatedEnergyLevels = neighboringPoints.mapNotNull { point ->
        energyGridMap[point]?.let { energyLevel ->
            if (energyLevel > 0 && point !in flashedPoints) {
                point to energyLevel.step()
            } else null
        }
    }

    val nextFlashedPoints = focusEnergyLevel?.let { if (it == 0) {
        flashedPoints + focusPoint
    } else flashedPoints } ?: flashedPoints
    val nextEnergyGridMap = energyGridMap + updatedEnergyLevels

    if (updatedEnergyLevels.isEmpty()) {
        return FlashResult(nextEnergyGridMap, nextFlashedPoints)
    }

    val result = updatedEnergyLevels
        .map { (p) -> p }
        .fold(nextEnergyGridMap to nextFlashedPoints) { acc, point ->
            val result = flashAt(point, acc.first, acc.second)
            acc.first + result.energyGridMap to acc.second + result.flashedPoints
        }

    return FlashResult(result.first, result.second)
}

fun Simulation.flashAt(
    row: Int,
    col: Int,
    energyGridMap: EnergyGridMap,
    flashedPoints: Set<Point> = setOf(),
): FlashResult {
    return flashAt(Point(row, col), energyGridMap, flashedPoints)
}

fun Simulation.flashAll(energyGridMap: EnergyGridMap): FlashResult {
    val startingPoints = energyGridMap
        .toList()
        .filter { (_, energyLevel) -> energyLevel == 0 }
        .map { (p) -> p }
    val initFlashedPoint = setOf<Point>()
    val result = startingPoints
        .fold(energyGridMap to initFlashedPoint) { acc, point ->
            val result = flashAt(point, acc.first, acc.second)
            acc.first + result.energyGridMap to acc.second + result.flashedPoints
        }
    return FlashResult(result.first, result.second)
}

fun Simulation.step(): SimulationStepResult {
    val energyGridMap = this.grid.toMap { _, energyLevel -> energyLevel.step() }
    val flashResult = flashAll(energyGridMap)
    val nextGrid = flashResult.energyGridMap.applyTo(this.grid)
    return SimulationStepResult(Simulation(nextGrid), flashResult.flashedPoints)
}

fun runSoluationPart1(energyGrid: EnergyGrid, stepCount: Int = 10) {
    println("Day 11 Solution: Part 1\n")

    var initSimulation = Simulation(energyGrid)
    val initStep = Triple(0, initSimulation, 0)
    var steps = (1..stepCount).fold(listOf(initStep)) { steps, stepCounter ->
        val (_, simulation, flashCount) = steps.last()
        val stepResult = simulation.step()
        steps + Triple(stepCounter, stepResult.simulation, stepResult.flashedPoints.size)
    }

    steps.forEach { (step, simulation, flashCount) ->
        println("After step $step")
        simulation.grid.prettyPrint()
        println()
    }

    val totalFlashes = steps.sumOf { (_, _, flashCount) -> flashCount }
    println("Total flashes: $totalFlashes")
}

fun runSolutionPart2(energyGrid: EnergyGrid) {
    println("Day 11 Solution: Part 2\n")

    var initSimulation = Simulation(energyGrid)
    val initStep = Triple(0, initSimulation, -1)

    val stepGenerator = generateSequence(initStep) { (step, simulation, prevFlashCount) ->
        if (prevFlashCount == simulation.grid.entryCount()) {
            null
        } else {
            val result = simulation.step()
            Triple(step + 1, result.simulation, result.flashedPoints.size)
        }
    }

    val steps = stepGenerator.toList()

    steps.forEach { (step, simulation, flashCount) ->
        println("After step $step")
        simulation.grid.prettyPrint()
        println()
    }
}


fun main() {
    val energyGrid = File("day11/src/main/resources/sampleInput.txt")
        .readLines()
        .map { it.toList().map { c -> c.digitToInt() } }

    runSoluationPart1(energyGrid, 100)
//    runSolutionPart2(energyGrid)
}
