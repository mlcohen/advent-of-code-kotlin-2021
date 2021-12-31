package org.adventofcode

import java.io.File
import kotlin.math.abs

data class Point(val x: Int, val y: Int)
data class Velocity(val x: Int, val y: Int)
data class Probe(val position: Point = Point(0, 0), val velocity: Velocity = Velocity(0, 0))
data class TargetArea(val topLeft: Point, val bottomRight: Point)

fun Velocity.incrementY(): Velocity {
    return this.copy(y = this.y + 1)
}

operator fun TargetArea.contains(p: Point): Boolean {
    val pointIsInsideVerticalBoundary = p.x >= topLeft.x && p.x <= bottomRight.x
    val pointIsInsideHorizontalBoundary = p.y <= topLeft.y && p.y >= bottomRight.y
    return pointIsInsideVerticalBoundary && pointIsInsideHorizontalBoundary
}

operator fun TargetArea.contains(p: Probe): Boolean {
    return p.position in this
}

fun Probe.before(t: TargetArea): Boolean {
    return (
        this.position.x <= t.bottomRight.x &&
        this.position.y >= t.bottomRight.y &&
        this !in t
    )
}

fun Probe.past(t: TargetArea): Boolean {
    return !this.before(t) && this !in t
}

fun Probe.step(): Probe {
    val nextVelocity = Velocity(
        x = when {
            this.velocity.x == 0 -> 0
            this.velocity.x > 0 -> this.velocity.x - 1
            this.velocity.x < 0 -> this.velocity.x + 1
            else -> throw error("Unexpected velocity.x ${this.velocity.x}")
        },
        y =  this.velocity.y - 1,
    )
    val nextPosition = Point(
        x = this.position.x + this.velocity.x,
        y = this.position.y + this.velocity.y,
    )
    return Probe(nextPosition, nextVelocity)
}

data class ProbeSimulatorRunResult(val probeSteps: List<Probe>, val hitTargetArea: Boolean) {
    val highestProbe: Probe? by lazy { probeSteps.maxByOrNull { it.position.y } }
}

fun velocityToReachTargetArea(targetArea: TargetArea): Velocity {
    val result = generateSequence(0 to 0) { (sum, counter) ->
        if (sum in targetArea.topLeft.x..targetArea.bottomRight.x) {
            null
        } else (sum + counter) to (counter + 1)
    }.last()
    val xVelocity = result.second - 1
    return Velocity(xVelocity, xVelocity)
}

object ProbeSimulator {
    fun runSimulation(probeStartVelocity: Velocity, targetArea: TargetArea): ProbeSimulatorRunResult {
        val startProbe = Probe(velocity = probeStartVelocity)
        val steps = generateSequence(startProbe) { probe ->
            if (probe.before(targetArea)) {
                probe.step()
            } else null
        }.toList()

        val lastProbeStep = steps.last()
        return ProbeSimulatorRunResult(steps, lastProbeStep in targetArea)
    }

    fun findMaximumProbeHeight(targetArea: TargetArea): Pair<Probe, Velocity>  {
        val initVelocity = velocityToReachTargetArea(targetArea)
        val sequence = generateSequence(initVelocity) { velocity  ->
            val (probeSteps) = runSimulation(velocity, targetArea)
            val finalProbeSteps = probeSteps.takeLast(2)
            val finalVerticalTravel = abs(finalProbeSteps[0].position.y - finalProbeSteps[1].position.y)
            if (finalVerticalTravel <= abs(targetArea.bottomRight.y)) {
                velocity.incrementY()
            } else null
        }
        val maxVelocity = sequence.toList().takeLast(2).reversed().last()
        val simResult = runSimulation(maxVelocity, targetArea)
        return (simResult.highestProbe ?: Probe()) to maxVelocity
    }
}

object ProbeSimulationPrettyPrinter {
    fun print(targetArea: TargetArea, probeSteps: List<Probe>) {
        val initPoint = Point(0, 0)
        val lastPoint = probeSteps.lastOrNull()?.position ?: initPoint
        val highestProbe = probeSteps.maxByOrNull { it.position.y }?.position ?: initPoint
        val points = setOf(initPoint, lastPoint, highestProbe, targetArea.topLeft, targetArea.bottomRight)
        val probePoints = probeSteps.map { it.position }.toSet()
        val minX = points.minOfOrNull { it.x } ?: 0
        val maxX = points.maxOfOrNull { it.x } ?: 0
        val minY = points.minOfOrNull { it.y } ?: 0
        val maxY = points.maxOfOrNull { it.y } ?: 0

        (maxY downTo minY).forEach { ypos ->
            val row = (minX until (maxX + 1)).joinToString("") { xpos ->
                when (Point(xpos, ypos)) {
                    initPoint -> "S"
                    in probePoints -> "#"
                    in targetArea -> "T"
                    else -> "."
                }
            }
            println(row)
        }
    }

    fun print(velocity: Velocity, targetArea: TargetArea) {
        val (probeSteps) = ProbeSimulator.runSimulation(velocity, targetArea)
        print(targetArea, probeSteps)
    }
}

fun runSolutionPart1(targetArea: TargetArea) {
    val (probe, velocity) = ProbeSimulator.findMaximumProbeHeight(targetArea)
    ProbeSimulationPrettyPrinter.print(velocity, targetArea)
    println()
    println("Height probe point: ${probe.position}")
    println("Max velocity: $velocity")
}

fun parseExpression(expression: String): TargetArea {
    // expression: target area: x=(<num>..<num>), y=(<num>..<num>)
    val (xRangeExpression, yRangeExpression) = expression.split(' ').drop(2)
    val rangeRegExp ="""[a-z]=(-?\d+)\.\.(-?\d+)""".toRegex()
    val rangeFor = { expr: String ->
        val values = rangeRegExp.find(expr)
        ?.let { it.groups.drop(1).map { group -> group?.value?.toInt() ?: 0 } } ?: listOf(0, 0)
        values.sorted()
    }
    val xRange = rangeFor(xRangeExpression)
    val yRange = rangeFor(yRangeExpression)
    val topLeft = Point(xRange.first(), yRange.last())
    val bottomRight = Point(xRange.last(), yRange.first())
    return TargetArea(topLeft, bottomRight)
}

fun main() {
    val data = File("day17/src/main/resources/puzzleInput.txt").readLines().first()
    val targetArea = parseExpression(data)
    runSolutionPart1(targetArea)
}
