package org.adventofcode

import java.io.File

fun groupInput(lines: List<String>): List<List<String>> {
    val groups = mutableListOf(mutableListOf<String>())
    return lines.fold(groups) { groups, line ->
        if (line.isEmpty()) {
            val group = mutableListOf<String>()
            groups.add(group)
        } else {
            groups.last().add(line)
        }
        groups
    }
}

fun buildCallNumbers(input: String): List<Int> {
    return input.split(',').map { it.toInt() }
}

fun buildBingoCardCollection(input: List<List<String>>): BingoCardCollection {
    val bingoCards = input
        .map { it.map { row ->
            row
                .trim()
                .split("""\s+""".toRegex())
                .map { num -> num.toInt() }
        } }
        .mapIndexed { idx, numbers -> BingoCard.create(numbers, id = idx) }
    return BingoCardCollection(bingoCards)
}

fun runPart1Solution(callNumbers: List<Int>, bingoCards: BingoCardCollection) {
    println("Day 4, Part 1 Solution")
    println("bingo card count = ${bingoCards.size}")

    var winningBingoCard: BingoCard? = null
    var winningCallNumber: Int? = null
    for (callNumber in callNumbers) {
        bingoCards.mark(callNumber)
        if (bingoCards.hasWinningBingoCards) {
            winningBingoCard = bingoCards.winningBingoCards.first()
            winningCallNumber = callNumber
            break
        }
    }

    if (winningBingoCard != null) {
        println("Found a winner!")
        println("winning call number $winningCallNumber")
        println("winning bingo card $winningBingoCard")
        println("winning numbers ${winningBingoCard.winningNumbers}")
        val winningScore = winningBingoCard.unmarkedNumbers.sum() * winningCallNumber!!
        println("winning score $winningScore")
    }
}

data class WinningBingoCardStats(
    val bingoCard: BingoCard,
    val callNumber: Int,
    val winningNumbers: List<Int>,
    val unmarkedNumbers: List<Int>,
)

fun runPart2Solution(callNumbers: List<Int>, bingoCards: BingoCardCollection) {
    println("Day 4, Part 2 Solution")
    println("bingo card count = ${bingoCards.size}")

    var orderedWinningBingoCards: MutableList<WinningBingoCardStats> = mutableListOf()
    var winningBingoCardSet: MutableSet<BingoCard> = mutableSetOf()

    for (callNumber in callNumbers) {
        bingoCards.mark(callNumber)
        bingoCards.winningBingoCards.forEach { bingoCard ->
            if (!winningBingoCardSet.contains(bingoCard)) {
                winningBingoCardSet.add(bingoCard)
                orderedWinningBingoCards.add(WinningBingoCardStats(
                    bingoCard = bingoCard,
                    winningNumbers = bingoCard.winningNumbers!!,
                    callNumber = callNumber,
                    unmarkedNumbers = bingoCard.unmarkedNumbers,
                ))
            }
        }
    }

    val stats = orderedWinningBingoCards.last()

    println("bingo card = ${stats.bingoCard}")
    println("call number = ${stats.callNumber}")
    println("winning numbers = ${stats.winningNumbers}")
    println("unmarked numbers = ${stats.unmarkedNumbers}")
    val winningScore = stats.unmarkedNumbers.sum() * stats.callNumber
    println("score = $winningScore")
}

fun main() {
    val rawInputLines = File("day04/src/main/resources/input.txt").readLines()
    val groupedInput = groupInput(rawInputLines)
    val callNumbers = buildCallNumbers(groupedInput.first().first())
    val bingoCards = buildBingoCardCollection(groupedInput.drop(1))

    runPart1Solution(callNumbers, bingoCards)
    runPart2Solution(callNumbers, bingoCards)
}
