package org.adventofcode

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals

val BIT_SEQ_LIST = toBitSequenceList(listOf(
    "00100",
    "11110",
    "10110",
    "10111",
    "10101",
    "01111",
    "00111",
    "11100",
    "10000",
    "11001",
    "00010",
    "01010",
))

class BitSequenceTest {

    @Test fun testGammaRateBitSequence() {
        val rating = BIT_SEQ_LIST.getGammaRateBitSequence()
        assertEquals(rating.toInt(), 22)
    }

    @Test fun testEpsilonRateBitSequence() {
        val rating = BIT_SEQ_LIST.getEpsilonRateBitSequence()
        assertEquals(rating.toInt(), 9)
    }

    @Test fun testOxygenGeneratorRating() {
        val rating = BIT_SEQ_LIST.getOxygenGeneratorRating()
        assertEquals(rating.toInt(), 23)
    }

    @Test fun testC02ScrubberRating() {
        val rating = BIT_SEQ_LIST.getC02ScrubberRating()
        assertEquals(rating.toInt(), 10)
    }
}
