package org.adventofcode

import kotlin.math.pow

typealias BitSequence = List<Boolean>
typealias BitSequenceList = List<BitSequence>

fun BitSequenceList.bitSize(): Int {
    return this[0].size
}

fun BitSequenceList.getBitMostCommonInColumn(column: Int): Boolean {
    if (column >= this.bitSize()) {
        throw error("Column out of bounds $column")
    }
    val numOnes = this.count() { it[column] }
    return numOnes.toDouble() >= (this.size.toDouble() / 2)
}

fun BitSequenceList.getBitLeastCommonInColumn(column: Int): Boolean {
    if (column >= this.bitSize()) {
        throw error("Column out of bounds $column")
    }
    return !this.getBitMostCommonInColumn(column)
}

fun BitSequenceList.isBitMostCommonInColumn(bit: Boolean, column: Int): Boolean {
    if (column >= this.bitSize()) {
        throw error("Column out of bounds $column")
    }
    val mcb = this.getBitMostCommonInColumn(column)
    return mcb == bit
}

fun BitSequenceList.isBitLeastCommonInColumn(bit: Boolean, column: Int): Boolean {
    if (column >= this.bitSize()) {
        throw error("Column out of bounds $column")
    }
    val lcb = this.getBitLeastCommonInColumn(column)
    return lcb == bit
}

fun BitSequenceList.filterByMostCommonColumnBit(column: Int): BitSequenceList {
    val mcb = this.getBitMostCommonInColumn(column)
    return this.filter { it[column] == mcb }
}

fun BitSequenceList.filterByLeastCommonColumnBit(column: Int): BitSequenceList {
    val lcb = this.getBitLeastCommonInColumn(column)
    return this.filter { it[column] == lcb }
}

fun BitSequenceList.getGammaRateBitSequence(): BitSequence {
    val bitLength = this[0].size
    return (0 until bitLength).foldIndexed(ArrayList()) { bitColumn, seq, _ ->
        seq += this.isBitMostCommonInColumn(bit = true, column = bitColumn)
        seq
    }
}

fun BitSequenceList.getEpsilonRateBitSequence(): BitSequence {
    return this.getGammaRateBitSequence().map { !it }
}

fun BitSequenceList.getOxygenGeneratorRating(bitColumn: Int = 0): BitSequence {
    if (bitColumn >= this[0].size) {
        throw error("Starting column is invalid $bitColumn")
    }

    if (this.size == 1) {
        return this[0]
    }

    val list = this.filterByMostCommonColumnBit(bitColumn)

    if (list.size == 1) {
        return list[0]
    }

    return list.getOxygenGeneratorRating(bitColumn + 1)
}

fun BitSequenceList.getC02ScrubberRating(bitColumn: Int = 0): BitSequence {
    if (bitColumn >= this[0].size) {
        throw error("Starting column is invalid $bitColumn")
    }

    if (this.size == 1) {
        return this[0]
    }

    val list = this.filterByLeastCommonColumnBit(bitColumn)

    if (list.size == 1) {
        return list[0]
    }

    return list.getC02ScrubberRating(bitColumn + 1)
}

fun toBitSequence(value: String): BitSequence {
    return value.map { it == '1' }
}

fun toBitSequenceList(value: List<String>): BitSequenceList {
    return value.map { value -> toBitSequence(value)}
}

fun BitSequence.toInt (): Int {
    return this.foldIndexed(0) { idx, sum, bit ->
        val bitPosition = this.size - 1 - idx
        val bitValue = if (bit) 1 else 0
        val result = bitValue * 2.toDouble().pow(bitPosition).toInt()
        sum + result
    }
}

fun BitSequence.toBinaryString (): String {
    return this.joinToString("") { if (it) "1" else "0" }
}