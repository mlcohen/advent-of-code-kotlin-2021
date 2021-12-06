package org.adventofcode

import java.io.File

const val SPAWNED_FISH = 8
const val REFRESHED_FISH = 6

fun advanceFishGrowthCycle(fish: List<Int>): List<Int> {
    return fish.map { if (it > 0) {
        Pair(it - 1, null)
    } else {
        Pair(REFRESHED_FISH, SPAWNED_FISH)
    } }.unzip().toList().flatten().filterNotNull()
}

fun simulateFishGrowth(
    init: List<Int>,
    days: Int,
    apply: ((Int, List<Int>) -> Unit)? = null,
): List<Int> {
    var next: List<Int> = init
    repeat(days) { day ->
        next = advanceFishGrowthCycle(next)
        if (apply != null) apply(day + 1, next)
    }
    return next
}

fun main() {
    val init = File("day06/src/main/resources/sampleInput.txt")
        .readLines().first().split(',').map { it.toInt() }
    println("init = $init")
    val fish = simulateFishGrowth(init, days=80) {
        day, fish -> println("After $day days: $fish")
    }
    println("total fish = ${fish.size}")
}
