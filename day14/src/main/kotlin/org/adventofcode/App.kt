package org.adventofcode

import java.io.File

val RULE_REGEX_PATTERN = """([\w]+) -> ([\w])""".toRegex()

typealias InsertionRules = Map<String, Char>
typealias CacheKey = Pair<String, Int>
typealias CharCountMap = Map<Char, Long>
typealias Cache = Map<CacheKey, CharCountMap>

fun CharCountMap.sort(): List<Pair<Char, Long>> {
    return this.toList().sortedWith { a, b ->
        when (val countCompare = a.second.compareTo(b.second)) {
            0 -> a.first.compareTo(b.first)
            else -> countCompare
        }
    }
}

object PolymerProcessor {
    private fun processPolymer(
        polymer: String,
        rules: InsertionRules,
        step: Int,
        cache: Cache,
    ): Pair<CharCountMap, Cache> {
        val (nextCharCountMap, cacheResult) = polymer
            .windowedSequence(2)
            .fold(mapOf<Char, Long>() to cache) { (thisAggregate, thisCache), nextPair ->
                val (result, nextCache) = processElementPair(nextPair, rules, step, thisCache)
                val nextAggregate = result.toList().fold(thisAggregate) { agg, (char, count) ->
                    agg + (char to (agg[char]?.let { it + count } ?: count))
                }
                nextAggregate to nextCache
            }
        val overlappingElements = polymer.substring(1, polymer.lastIndex)
        val charCountMapResult = overlappingElements
            .fold(nextCharCountMap) { map, char ->
                var count = map[char]?.let { it - 1 } ?: 1
                map + (char to count)
            }
        return charCountMapResult to cacheResult
    }

    private fun processElementPair(
        pair: String,
        rules: InsertionRules,
        step: Int,
        cache: Cache,
    ): Pair<CharCountMap, Cache> {
        val cachedValue = cache[pair to step]
        if (cachedValue != null) {
            return cachedValue to cache
        }

        if (step == 0) {
            val expandedResult = pair.toList().fold(mapOf<Char, Long>()) { map, char ->
                map + (char to map.getOrDefault(char, 0L) + 1L)
            }
            val nextCache = cache + (Pair(pair, step) to expandedResult)
            return expandedResult to nextCache
        }

        val insertedElem = rules[pair] ?: throw error("No insertion rule for pair $pair")
        val polymer = "${pair[0]}$insertedElem${pair[1]}"
        val (aggregateResult, nextCache) = processPolymer(polymer, rules, step - 1, cache)
        val cacheResult = nextCache + (Pair(pair, step) to aggregateResult)

        return aggregateResult to cacheResult
    }

    fun process(template: String, rules: InsertionRules, steps: Int): CharCountMap {
        val cache = mapOf<CacheKey, CharCountMap>()
        val result = processPolymer(template, rules, steps, cache)
        return result.first
    }
}

fun summarizePolymerProcessingResult(result: CharCountMap) {
    val sortedCharCounts = result.sort()
    val leastCommon = sortedCharCounts.first()
    val mostCommon = sortedCharCounts.last()
    val score = mostCommon.second - leastCommon.second

    println("Sort char-count: $sortedCharCounts")
    println("Most common char count: $leastCommon")
    println("Least common char count: $mostCommon")
    println("Score: $score")
}

fun runSolutionPart1(template: String, rules: Map<String, Char>, steps: Int = 10) {
    println("Day 14 Solution: Part 1 (Basic Implementation)\n")
    val polymer = (0 until steps).fold(template) { polymer, _ ->
        polymer
            .windowedSequence(2)
            .mapNotNull { rules[it]?.let { elem -> "${it[0]}$elem${it[1]}" } }
            .mapIndexed { idx, chunk -> if (idx == 0) chunk else chunk.substring(1) }
            .joinToString("")
    }
    val charCounts: CharCountMap = polymer.asSequence().fold(mapOf<Char, Long>()) { map, char ->
        map + (char to map.getOrDefault(char, 0L) + 1L)
    }

    summarizePolymerProcessingResult(charCounts)
}

fun runSolutionPart2(template: String, rules: Map<String, Char>, steps: Int = 10) {
    println("Day 14 Solution: Part 2\n")
    val result = PolymerProcessor.process(template, rules, steps)
    summarizePolymerProcessingResult(result)
}

fun main() {
    val input = File("day14/src/main/resources/sampleInput.txt").readLines()
    val template = input.first()
    val rules = input.drop(2).mapNotNull {
        val result = RULE_REGEX_PATTERN.matchEntire(it)
        result?.groupValues?.let { (_, fromPair, toChar) -> fromPair to toChar[0] }
    }.toMap()

    runSolutionPart1(template, rules, 10)
    println()
    runSolutionPart2(template, rules, 10)


}
