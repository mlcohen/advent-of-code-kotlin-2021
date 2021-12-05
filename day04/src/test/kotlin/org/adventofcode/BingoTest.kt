package org.adventofcode

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals

class BingoTest {

    @Test fun testBingoCards() {
        val bingoCard1 = BingoCard.create(id = 1, numbers = listOf(
            22, 13, 17, 11,  0,
            8,   2, 23,  4, 24,
            21,  9, 14, 16,  7,
             6, 10,  3, 18,  5,
             1, 12, 20, 15, 19,
        ))
        val bingoCard2 = BingoCard.create(id = 2, numbers = listOf(
             3, 15,  0,  2, 22,
             9, 18, 13, 17,  5,
            19,  8,  7, 25, 23,
            20, 11, 10, 24,  4,
            14, 21, 16, 12,  6,
        ))
        val bingoCard3 = BingoCard.create(id = 3, numbers = listOf(
            14, 21, 17, 24,  4,
            10, 16, 15,  9, 19,
            18,  8, 23, 26, 20,
            22, 11, 13,  6,  5,
             2,  0, 12,  3,  7,
        ))
        val bingoCards = listOf(bingoCard1, bingoCard2, bingoCard3)

        listOf(7, 4, 9, 5, 11).forEach { number -> bingoCards.forEach { it.mark(number) }}

        assertEquals(bingoCard1.isWinner, false)
        assertEquals(bingoCard2.isWinner, false)
        assertEquals(bingoCard3.isWinner, false)

        listOf(17, 23, 2, 0, 14, 21).forEach { number -> bingoCards.forEach { it.mark(number) }}

        assertEquals(bingoCard1.isWinner, false)
        assertEquals(bingoCard2.isWinner, false)
        assertEquals(bingoCard3.isWinner, false)

        bingoCards.forEach { it.mark(24) }

        assertEquals(bingoCard1.isWinner, false)
        assertEquals(bingoCard2.isWinner, false)
        assertEquals(bingoCard3.isWinner, true)
        assertEquals(bingoCard3.winningNumbers, listOf(14, 21, 17, 24, 4))
        assertEquals(bingoCard3.unmarkedNumbers.sum(), 188)
    }
}
