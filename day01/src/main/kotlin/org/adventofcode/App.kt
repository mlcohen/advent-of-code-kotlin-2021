package org.adventofcode

import java.io.File

fun main() {
    val input = File("day01/src/main/resources/input.txt").readLines().map(String::toInt)
    val count = ThreeMeasurementSlidingWindow.count(input)
    println("count = $count")
}
