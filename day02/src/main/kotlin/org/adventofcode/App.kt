package org.adventofcode

import java.io.File

fun parseSubmarineCommandLines(commands: List<String>): List<SubmarineCommand> {
     return commands.map { it.split(" ").let { (action, steps) ->
         SubmarineCommand(
             action = SubmarineActionType.valueOf(action.uppercase()),
             steps = steps.toInt(),
         )
     }}
}

fun runSolutionPart1(commands: List<SubmarineCommand>): Unit {
    val loc = SubmarineLocation().moveBy(commands)
    println("Day 2, Part 1")
    println("loc = $loc")
    println("area = ${loc.totalArea}")
}

fun runSolutionPart2(commands: List<SubmarineCommand>): Unit {
    val loc = SubmarineAimLocation().moveBy(commands)
    println("Day 2, Part 2")
    println("loc = $loc")
    println("area = ${loc.totalArea}")
}

fun main() {
    val input = File("day02/src/main/resources/input.txt").readLines()
    val commands = parseSubmarineCommandLines(input)
    runSolutionPart2(commands)
}
