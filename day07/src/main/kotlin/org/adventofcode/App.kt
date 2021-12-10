package org.adventofcode

import java.io.File
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

data class Fuel(val cost: Int, val position: Int)

typealias MovementFuelCostFn = (fromPosition: Int, toPosition: Int) -> Int

object Solution {

    fun fuelCostToPosition(
        toPosition: Int,
        initialCrabPositions: List<Int>,
        movementFuelCost: MovementFuelCostFn,
    ): Int {
        return initialCrabPositions.fold(0) { totalFuelCost, fromCrabPosition ->
            totalFuelCost + movementFuelCost(fromCrabPosition, toPosition)
        }
    }

    fun minFuelFor(
        initialCrabPositions: List<Int>,
        movementFuelCost: MovementFuelCostFn,
    ): Fuel {
        val sortedPositions = initialCrabPositions.sorted()
        val firstPostion = sortedPositions.first()
        val lastPosition = sortedPositions.last()
        var minFuelCost: Fuel? = null;
        for (toPosition in firstPostion..lastPosition) {
            val fuelCost = fuelCostToPosition(toPosition, initialCrabPositions, movementFuelCost)
            if (minFuelCost == null || fuelCost < minFuelCost.cost) {
                minFuelCost = Fuel(fuelCost, toPosition)
            }
        }
        return minFuelCost!!
    }

}

fun simpleMovementFuelCost(fromPosition: Int, toPosition: Int): Int {
    return abs(toPosition - fromPosition)
}

class ExpensiveMovementFuelCost {
    private val cache = mutableMapOf<Int, Int>()

    fun calculate(fromPosition: Int, toPosition: Int): Int {
        val distance = abs(fromPosition - toPosition)

        if (distance == 0) {
            return 0
        }

        val cachedEntry = cache[distance]

        if (cachedEntry != null) {
            return cachedEntry
        }

        val maxPosition = max(fromPosition, toPosition)
        val minPosition = min(fromPosition, toPosition)
        val cost = distance + this.calculate(minPosition, maxPosition - 1)

        cache[distance] = cost

        return cost
    }
}

fun runSimpleMoveMinFuelCostSolution(input: List<Int>) {
    println("Min Fuel Cost: Simple Movement")
    val minFuel = Solution.minFuelFor(input, movementFuelCost = ::simpleMovementFuelCost)
    println("result = $minFuel")
}

fun runExpensiveMovementMinFuelCostSolution(input: List<Int>) {
    println("Min Fuel Cost: Expensive Movement")
    val expensiveMovementFuelCost = ExpensiveMovementFuelCost()
    val minFuel = Solution.minFuelFor(input, movementFuelCost = expensiveMovementFuelCost::calculate)
    println("result = $minFuel")
}

fun main() {
    val input = File("day07/src/main/resources/sampleInput.txt")
        .readLines().first().split(',').map { it.toInt() }
    runSimpleMoveMinFuelCostSolution(input)
    runExpensiveMovementMinFuelCostSolution(input)
}