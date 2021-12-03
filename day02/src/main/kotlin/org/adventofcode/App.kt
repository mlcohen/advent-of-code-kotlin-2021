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

fun main() {
    val input = File("day02/src/main/resources/input.txt").readLines()
    val commands = parseSubmarineCommandLines(input)
    val loc = SubmarineLocation().moveBy(commands)

    println("loc = $loc")
    println("area = ${loc.totalArea}")
}
