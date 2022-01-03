package org.adventofcode

import java.io.File

fun findSummationUpperLimit(startIndex: Int = 0, term: ((Int) -> Int), done: ((Int) -> Boolean)): Int {
    val sequence = generateSequence(0 to startIndex) { (sum, index) ->
        val value = term(index) + sum
        if (done(value)) null else value to index + 1
    }
    return sequence.last().second + 1
}

data class Point(val x: Int, val y: Int)
data class TargetArea(val topLeft: Point, val bottomRight: Point)
data class Velocity(val x: Int, val y: Int)
data class Probe(val position: Point = Point(0, 0), val velocity: Velocity = Velocity(0, 0)) {
    companion object {
        fun withXVelocity(x: Int): Probe = Probe(velocity = Velocity(x = x, y = 0))
        fun withVelocity(x: Int, y: Int): Probe = Probe(velocity = Velocity(x, y))
        fun withVelocity(velocity: Velocity): Probe = Probe(velocity = velocity)
        fun minXVelocityToReach(targetArea: TargetArea): Int =
            findSummationUpperLimit(term = { 1 + it }, done = { it > targetArea.topLeft.x })
        fun maxXVelocityToReach(targetArea: TargetArea): Int = targetArea.bottomRight.x
    }
}

fun Velocity.incrementY(): Velocity = this.copy(y = this.y + 1)
fun Velocity.decrementY(): Velocity = this.copy(y = this.y - 1)

operator fun TargetArea.contains(p: Point): Boolean {
    val pointIsInsideVerticalBoundary = p.x >= topLeft.x && p.x <= bottomRight.x
    val pointIsInsideHorizontalBoundary = p.y <= topLeft.y && p.y >= bottomRight.y
    return pointIsInsideVerticalBoundary && pointIsInsideHorizontalBoundary
}

operator fun TargetArea.contains(p: Probe): Boolean = p.position in this

fun Probe.before(t: TargetArea): Boolean {
    return (
        this.position.x <= t.bottomRight.x &&
        this.position.y >= t.bottomRight.y &&
        this !in t
    )
}

fun Probe.canHit(targetArea: TargetArea): Boolean {
    val lastProbeStep = generateSequence(this) { probe -> probe.step() }
        .takeWhile { probe -> (
            probe.velocity.x > 0 &&
            probe.position.x <= targetArea.bottomRight.x
        ) }
        .last()

    return lastProbeStep.position.x in targetArea.topLeft.x..targetArea.bottomRight.x
}

fun Probe.canNotHit(targetArea: TargetArea): Boolean = !this.canHit(targetArea)

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

typealias ProbeSegment = Pair<Probe, Probe>

fun ProbeSegment.overshot(targetArea: TargetArea): Boolean {
    val p1 = this.first
    val p2 = this.second
    return (
            p1.position.x <= targetArea.bottomRight.x &&
            p1.position.y > targetArea.topLeft.y &&
            p2.position.x > targetArea.bottomRight.x &&
            p2.position.y >= targetArea.bottomRight.y
        ) || (
            p1.position.x in targetArea.topLeft.x..targetArea.bottomRight.x &&
            p1.position.y > targetArea.topLeft.y &&
            p2.position.x > targetArea.bottomRight.x &&
            p2.position.y <= targetArea.bottomRight.y
        )
}

fun ProbeSegment.undershot(targetArea: TargetArea): Boolean {
    val p1 = this.first
    val p2 = this.second
    return (
        p1.position.x < targetArea.topLeft.x &&
        p1.position.y >= targetArea.bottomRight.y &&
        p2.position.y < targetArea.bottomRight.y &&
        p2.position.x <= targetArea.bottomRight.x
    )
}

fun ProbeSegment.droppedPast(targetArea: TargetArea): Boolean {
    val p1 = this.first
    val p2 = this.second
    return (
        p1.position.x == p2.position.x &&
        p1.position.x in (targetArea.topLeft.x..targetArea.bottomRight.x) &&
        p1.position.y > targetArea.topLeft.y &&
        p2.position.y < targetArea.bottomRight.y
    )
}

fun ProbeSegment.droppedBefore(targetArea: TargetArea): Boolean {
    val p1 = this.first
    val p2 = this.second
    return (
        p1.position.x == p2.position.x &&
        p1.position.x < targetArea.topLeft.x
    )
}

fun ProbeSegment.droppedAfter(targetArea: TargetArea): Boolean {
    val p1 = this.first
    val p2 = this.second
    return (
        p1.position.x == p2.position.x &&
        p1.position.x > targetArea.bottomRight.x
    )
}

fun ProbeSegment.passedThrough(targetArea: TargetArea): Boolean {
    val p1 = this.first
    val p2 = this.second
    return (
        p1.position.x < targetArea.topLeft.x &&
        p1.position.y <= targetArea.topLeft.y &&
        p1.position.y >= targetArea.bottomRight.y &&
        p2.position.x > targetArea.bottomRight.x &&
        p2.position.y <= targetArea.topLeft.y
    )
}

typealias ProbePath = List<Probe>

fun ProbePath.lastSegment(): ProbeSegment {
    if (this.isEmpty()) return Probe() to Probe()
    if (this.size == 1) return this[0] to this[0]
    val probes = this.takeLast(2)
    return probes.first() to probes.last()
}

fun ProbePath.maxPoint(): Point {
    if (this.isEmpty()) return Point(0, 0)

    val remainder = if (this[0].position == Point(0, 0)) this.drop(1) else this
    return remainder.maxByOrNull { it.position.y }?.position ?: Point(0, 0)
}

enum class ProbeTrajectoryTargetOutcome {
    HIT,
    OVERSHOT,
    UNDERSHOT,
    PASSED_THROUGH,
    DROPPED_PAST,
    DROPPED_BEFORE,
    DROPPED_AFTER,
    UNKNOWN,
}

data class ProbeSimulatorRunResult(val path: ProbePath, val outcome: ProbeTrajectoryTargetOutcome)

object ProbeSimulator {
    private fun traceSteps(startProbe: Probe, targetArea: TargetArea): List<Probe> {
        return generateSequence(startProbe) { probe ->
            if (probe.before(targetArea)) {
                probe.step()
            } else null
        }.toList()
    }

    private fun determinePathOutcome(path: ProbePath, targetArea: TargetArea): ProbeTrajectoryTargetOutcome {
        val (firstProbe, lastProbe) = path.takeLast(2)
        val segment = (firstProbe to lastProbe)
        return when {
            lastProbe in targetArea -> ProbeTrajectoryTargetOutcome.HIT
            segment.droppedBefore(targetArea) -> ProbeTrajectoryTargetOutcome.DROPPED_BEFORE
            segment.droppedAfter(targetArea) -> ProbeTrajectoryTargetOutcome.DROPPED_AFTER
            segment.droppedPast(targetArea) -> ProbeTrajectoryTargetOutcome.DROPPED_PAST
            segment.passedThrough(targetArea) -> ProbeTrajectoryTargetOutcome.PASSED_THROUGH
            segment.overshot(targetArea) -> ProbeTrajectoryTargetOutcome.OVERSHOT
            segment.undershot(targetArea) -> ProbeTrajectoryTargetOutcome.UNDERSHOT
            else -> ProbeTrajectoryTargetOutcome.UNKNOWN
        }
    }

    fun run(probeStartVelocity: Velocity, targetArea: TargetArea): ProbeSimulatorRunResult {
        val startProbe = Probe(velocity = probeStartVelocity)
        val path = traceSteps(startProbe, targetArea)
        val outcome = determinePathOutcome(path, targetArea)
        return ProbeSimulatorRunResult(path, outcome)
    }
}

data class MaximumProbeHeightFindResult(
    val path: ProbePath,
    val velocity: Velocity,
    val targetReachable: Boolean = true,
)

object MaximumProbeHeight {
    fun findFor(targetArea: TargetArea): MaximumProbeHeightFindResult  {
        val xVelocity = Probe.minXVelocityToReach(targetArea)
        return findForXVelocity(xVelocity, targetArea)
    }

    fun findForXVelocity(xVelocity: Int, targetArea: TargetArea): MaximumProbeHeightFindResult {
        if (Probe.withXVelocity(xVelocity).canNotHit(targetArea)) {
            return MaximumProbeHeightFindResult(
                path = listOf(),
                velocity = Velocity(xVelocity, 0),
                targetReachable = false,
            )
        }

        val yVelocity = findYVelocityForXVelocityToReachTargetArea(xVelocity, targetArea)
        val sequence = generateSequence(Velocity(xVelocity, yVelocity)) { velocity  ->
            val (path) = ProbeSimulator.run(velocity, targetArea)
            val segment = path.lastSegment()
            when {
                segment.droppedPast(targetArea) && segment.first.position.y == 0 -> null
                segment.overshot(targetArea) -> null
                else -> velocity.incrementY()
            }
        }

        val maxVelocity = sequence.toList().takeLast(2).reversed().last()
        val simResult = ProbeSimulator.run(maxVelocity, targetArea)
        return MaximumProbeHeightFindResult(simResult.path, maxVelocity)
    }

    private fun findYVelocityForXVelocityToReachTargetArea(
        xVelocity: Int,
        targetArea: TargetArea,
        yVelocity: Int = 0,
    ): Int {
        val result = ProbeSimulator.run(Velocity(xVelocity, yVelocity), targetArea)
        return when (result.outcome) {
            ProbeTrajectoryTargetOutcome.HIT -> yVelocity
            ProbeTrajectoryTargetOutcome.OVERSHOT,
            ProbeTrajectoryTargetOutcome.DROPPED_PAST -> {
                findYVelocityForXVelocityToReachTargetArea(xVelocity, targetArea, yVelocity - 1)
            }
            ProbeTrajectoryTargetOutcome.UNDERSHOT -> {
                findYVelocityForXVelocityToReachTargetArea(xVelocity, targetArea, yVelocity + 1)
            }
            else -> throw error("Unexpected simulation outcome ${result.outcome}, X-velocity $xVelocity, Y-velocity $yVelocity")
        }
    }
}


object TargetAreaHitVelocities {
    fun findAllHitVelocitiesFor(xVelocity: Int, targetArea: TargetArea): Set<Velocity> {
        if (Probe.withXVelocity(xVelocity).canNotHit(targetArea)) {
            return setOf()
        }

        val maxProbeHeightFindResult = MaximumProbeHeight.findForXVelocity(xVelocity, targetArea)

        if (!maxProbeHeightFindResult.targetReachable) {
            return setOf()
        }

        val initMaxVelocity = maxProbeHeightFindResult.velocity
        val initTrackState = (Probe() to Probe()) to Velocity(0, 0)

        val sequence = generateSequence(initMaxVelocity to initTrackState) { (velocity) ->
            val simResult = ProbeSimulator.run(velocity, targetArea)
            val segment = simResult.path.lastSegment()
            when {
                segment.undershot(targetArea) || segment.passedThrough(targetArea) -> null
                segment.droppedPast(targetArea) && segment.first.position.y == 0 -> null
                else -> velocity.decrementY() to (segment to velocity)
            }
        }

        return sequence.toList()
            .map { it.second }
            .filter { (segment) -> segment.second in targetArea }
            .map { it.second }
            .toSet()
    }

    fun findAllHitVelocitiesFor(targetArea: TargetArea): Set<Velocity> {
        val minXVelocity = Probe.minXVelocityToReach(targetArea)
        val maxXVelocity = Probe.maxXVelocityToReach(targetArea)
        return (minXVelocity..maxXVelocity).fold(setOf()) { velocities, xVelocity ->
            velocities + findAllHitVelocitiesFor(xVelocity, targetArea)
        }
    }
}

object ProbeSimulationPrettyPrinter {
    fun print(targetArea: TargetArea, probeSteps: ProbePath) {
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
}

object TargetAreaExpressionParser {
    fun parse(expression: String): TargetArea {
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

    fun fromFile(pathname: String): TargetArea {
        val data = File(pathname).readLines().first()
        return parse(data)
    }
}

fun runSolutionPart1(targetArea: TargetArea) {
    println("Day 17 Solution: Part 1")
    val result = MaximumProbeHeight.findFor(targetArea)
    println("Highest point: ${result.path.maxPoint()}")
    println("Max velocity: ${result.velocity}")
//    println()
//    ProbeSimulationPrettyPrinter.print(targetArea, result.path)
}

fun runSolutionPart2(targetArea: TargetArea) {
    println("Day 17 Solution: Part 2")
    val velocities = TargetAreaHitVelocities.findAllHitVelocitiesFor(targetArea)
    println("Velocity count: ${velocities.size}")
}

fun main() {
    val targetArea = TargetAreaExpressionParser.fromFile("day17/src/main/resources/puzzleInput.txt")
    runSolutionPart1(targetArea)
    println()
    runSolutionPart2(targetArea)
}
