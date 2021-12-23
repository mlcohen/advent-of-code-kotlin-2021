package org.adventofcode

import java.io.File

val RULE_REGEX_PATTERN = """([\w]+) -> ([\w])""".toRegex()

fun runSolutionPart1(template: String, rules: Map<String, String>) {
    println("Day 14 Solution: Part 1 (Basic Implementation)")
    val steps = 10
    val polymer = (0 until steps).fold(template) { polymer, _ ->
        polymer
            .windowedSequence(2)
            .mapNotNull { rules[it]?.let { elem -> "${it[0]}$elem${it[1]}" } }
            .mapIndexed { idx, chunk -> if (idx == 0) chunk else chunk.substring(1) }
            .joinToString("")
    }
    val charCounts = polymer.asSequence().fold(mapOf<Char, Int>()) { map, char ->
        map + (char to map.getOrDefault(char, 0) + 1)
    }
    val mostCommonCharCount = charCounts.maxOf { (_, count) -> count }
    val leastCommonCharCount = charCounts.minOf { (_, count) -> count }
    val score = mostCommonCharCount - leastCommonCharCount

    println("Most common char count: $mostCommonCharCount")
    println("Least common char count: $leastCommonCharCount")
    println("Score: $score")
}

fun main() {
    val input = File("day14/src/main/resources/puzzleInput.txt").readLines()
    val template = input.first()
    val rules = input.drop(2).mapNotNull {
        val result = RULE_REGEX_PATTERN.matchEntire(it)
        result?.groupValues?.let { (_, fromPair, toChar) -> fromPair to toChar }
    }.toMap()

    runSolutionPart1(template, rules)
}
