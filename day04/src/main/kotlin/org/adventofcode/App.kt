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

fun main() {
    val rawInputLines = File("day04/src/main/resources/input.txt").readLines()
    val groupedInput = groupInput(rawInputLines)
    val callNumbers = buildCallNumbers(groupedInput.first().first())
    val bingoCards = buildBingoCardCollection(groupedInput.drop(1))

    println("bingo card count = ${bingoCards.size}")

    var winningBingoCard: BingoCard? = null
    var winningCallNumber: Int? = null
    for (callNumber in callNumbers) {
        bingoCards.mark(callNumber)
        if (bingoCards.hasWinningBingoCard) {
            winningBingoCard = bingoCards.winningBingoCard
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
        println("winning score ${winningScore}")
    }
}
