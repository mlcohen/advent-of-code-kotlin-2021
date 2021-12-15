package org.adventofcode

data class EncodedDigitSegment(
    val position: DigitSegmentPosition,
    val chars: Set<Char> = setOf()
) {
    val isAssigned = chars.isNotEmpty()
    val isAmbiguous = chars.size > 1
    val isFixed = chars.size == 1

    override fun toString(): String {
        val charsString = chars.toList().joinToString("")
        return "EncodedDigitSegment(${position}, chars=${charsString})"
    }
}

fun EncodedDigitSegment.intersect(segment: EncodedDigitSegment): EncodedDigitSegment {
    val chars = this.chars.intersect(segment.chars)
    return this.copy(chars = chars)
}

fun EncodedDigitSegment.removeChar(c: Char): EncodedDigitSegment {
    val remainingChars = this.chars.subtract(setOf(c))
    return this.copy(chars = remainingChars)
}

fun EncodedDigitSegment.removeChars(cs: Set<Char>): EncodedDigitSegment {
    val remainingChars = this.chars.subtract(cs)
    return this.copy(chars = remainingChars)
}

fun EncodedDigitSegment.removeChars(cs: List<Char>): EncodedDigitSegment {
    return this.removeChars(cs.toSet())
}

fun EncodedDigitSegment.clearChars(): EncodedDigitSegment {
    return this.copy(chars = setOf())
}

fun EncodedDigitSegment.assignChar(c: Char): EncodedDigitSegment {
    return this.copy(chars = setOf(c))
}

fun EncodedDigitSegment.assignChars(cs: Set<Char>): EncodedDigitSegment {
    return this.copy(chars = cs)
}