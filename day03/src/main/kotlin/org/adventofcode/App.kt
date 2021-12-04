package org.adventofcode

import java.io.File

fun runPart1Solution (input: BitSequenceList) {
    val gammaRate = input.getGammaRateBitSequence()
    val epsilonRate = input.getEpsilonRateBitSequence()

    println("*** Solution for Day 3, part 1")
    println("gamma rate = ${gammaRate.toInt()} (${gammaRate.toBinaryString()})")
    println("epsilon rate = ${epsilonRate.toInt()}, (${epsilonRate.toBinaryString()})")
    println("power consumption = ${gammaRate.toInt() * epsilonRate.toInt()}")
}

fun runPart2Solution (input: BitSequenceList) {
    val oxygenRating = input.getOxygenGeneratorRating()
    val c02Rating = input.getC02ScrubberRating()

    println("*** Solution for Day 3, part 2")
    println("oxygen generator rating = ${oxygenRating.toInt()} (${oxygenRating.toBinaryString()})")
    println("C02 scrubber rating = ${c02Rating.toInt()} (${c02Rating.toBinaryString()})")
    println("life support rating = ${oxygenRating.toInt() * c02Rating.toInt()}")
}

fun main() {
    val rawInput = File("day03/src/main/resources/input.txt").readLines()
    val input = toBitSequenceList(rawInput)
    runPart1Solution(input)
    runPart2Solution(input)
}
