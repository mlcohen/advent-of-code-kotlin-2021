package org.adventofcode

import java.io.File

const val SPAWNED_FISH = 8
const val REFRESHED_FISH = 6

class OriginalFishGrowthSimulatorSolution {
    companion object {
        private fun advanceFishGrowthCycle(fish: List<Int>): List<Int> {
            return fish.map { if (it > 0) {
                Pair(it - 1, null)
            } else {
                Pair(REFRESHED_FISH, SPAWNED_FISH)
            } }.unzip().toList().flatten().filterNotNull()
        }

        fun simulate(
            init: List<Int>,
            days: Int,
            observe: ((Int, List<Int>) -> Unit)? = null,
        ): Int {
            var next: List<Int> = init
            repeat(days) { day ->
                next = advanceFishGrowthCycle(next)
                observe?.let { it(day + 1, next) }
            }
            return next.size
        }
    }
}

data class ObserveFishSimulationDetails(
    val day: Int,
    val fishCycle: Int,
    val sequence: Int,
    val birthedFish: Long? = 0,
    val cacheHit: Boolean? = false,
    val cachedValue: Long? = 0L,
)

typealias ObserveFishSimulationFn = (
    details: ObserveFishSimulationDetails,
) -> Unit

data class CacheKey(
    val day: Int,
    val fishCycle: Int,
    val birthedFish: Long,
)

class ImprovedFishGrowthSimulatorSolution {
    companion object {
        private fun advance(
            maxDays: Int,
            fishCycle: Int,
            day: Int = 0,
            birthedFish: Long = 0L,
            sequence: Int = 1,
            observe: ObserveFishSimulationFn? = null,
            cache: MutableMap<CacheKey, Long> = mutableMapOf(),
        ): Long {
            observe?.let {
                it(ObserveFishSimulationDetails(
                    sequence = sequence,
                    day = day,
                    fishCycle = fishCycle,
                    birthedFish = birthedFish
                ))
            }

            val cacheKey = CacheKey(day, fishCycle, birthedFish)
            if (cache.contains(cacheKey)) {
                return cache[cacheKey]!!
            }

            if (day == maxDays) {
                return birthedFish
            }

            if (fishCycle == 0) {
                val v1 = advance(
                    maxDays,
                    REFRESHED_FISH,
                    day + 1,
                    birthedFish + 1,
                    sequence,
                    observe = observe,
                    cache = cache,
                )
                val v2 = advance(
                    maxDays,
                    SPAWNED_FISH,
                    day + 1,
                    0,
                    sequence + 1,
                    observe = observe,
                    cache = cache,
                )
                cache[CacheKey(day + 1, REFRESHED_FISH, birthedFish + 1)] = v1
                cache[CacheKey(day + 1, SPAWNED_FISH, 0)] = v2
                return v1 + v2
            }

            val result = advance(
                maxDays,
                fishCycle - 1,
                day + 1,
                birthedFish,
                sequence,
                observe = observe,
                cache = cache,
            )

            cache[CacheKey(day + 1, fishCycle - 1, birthedFish)] = result

            return result
        }

        fun simulate(
            init: List<Int>,
            days: Int,
            observe: ObserveFishSimulationFn? = null,
        ): Long {
            val cache: MutableMap<CacheKey, Long> = mutableMapOf()
            return init.fold(init.size.toLong()) { sum, fish ->
                sum + advance(maxDays = days, fishCycle = fish, day = 0, observe = observe, cache = cache)
            }
        }
    }
}

fun runPart1Solution(simulatedDays: Int, values: List<Int>) {
    println("Solution 1: Simulate Fish Pop")
    val result = OriginalFishGrowthSimulatorSolution.simulate(values, days=simulatedDays)
//    {
//        day, fish -> println("After $day days: $fish")
//    }
    println("total fish = $result")
}

fun runPart2Solution(simulatedDays: Int, values: List<Int>) {
    println("Solution 2: Simulate Fish Pop")
    val fishPopulation2 = ImprovedFishGrowthSimulatorSolution.simulate(values, days=simulatedDays)
//    { details ->
//        println("[${details.sequence}] After ${details.day} days: fish cycle ${details.fishCycle}, birthed fish = ${details.birthedFish}")
//    }
    println("total fish = $fishPopulation2")
}


fun main() {
    val values = File("day06/src/main/resources/sampleInput.txt")
        .readLines().first().split(',').map { it.toInt() }
    val simulatedDays = 80
    runPart1Solution(simulatedDays, values)
    runPart2Solution(simulatedDays, values)
}
