package org.adventofcode

data class EncodedDigit(
    val chars: List<Char>,
    val segments: List<EncodedDigitSegment> = listOf(),
) {
    val size = chars.size

    override fun toString(): String {
        val valueString = if (size == 0) "<>" else chars.joinToString("")
        val segmentStrings = segments
            .filter { it.isAssigned }
            .joinToString(", ") {
                "${it.position}=${it.chars.joinToString("")}"
            }
        return "EncodedDigit(${valueString}, segments=[$segmentStrings])"
    }
}

fun EncodedDigit.charsToString(): String {
    return chars.joinToString("")
}

typealias MapEncodedDigitSegmentsFn = (segment: EncodedDigitSegment) -> EncodedDigitSegment
typealias MapEncodedDigitSegmentsIndexedFn = (index: Int, segment: EncodedDigitSegment) -> EncodedDigitSegment

fun EncodedDigit.mapSegments(fn: MapEncodedDigitSegmentsFn): EncodedDigit {
    val mappedSegments = this.segments.map { fn(it) }
    return this.copy(segments = mappedSegments)
}

fun EncodedDigit.mapSegmentsIndexed(fn: MapEncodedDigitSegmentsIndexedFn): EncodedDigit {
    val mappedSegments = this.segments.mapIndexed { idx, segment -> fn(idx, segment) }
    return this.copy(segments = mappedSegments)
}

fun EncodedDigit.resolvedSegments(): List<EncodedDigitSegment> {
    return this.segments
        .groupBy { it.chars }
        .toList()
        .filter { (charset, segments) ->
            charset.size == 1 && segments.size == 1
        }
        .map { (_, segments) -> segments.first() }
}

fun EncodedDigit.assignCharsToSegments(vararg assignments: Pair<String, DigitSegmentPosition>): EncodedDigit {
    val positionToChars = assignments.associate { (chars, position) -> position to chars.toSet() }
    return this.mapSegments { segment ->
        if (positionToChars.contains(segment.position)) {
            segment.assignChars(positionToChars[segment.position]!!)
        } else {
            segment
        }
    }
}

fun EncodedDigit.reduceFrom(fromDigit: EncodedDigit): EncodedDigit {
    return this.mapSegmentsIndexed { idx, segment ->
        val fromSegment = fromDigit.segments[idx]
        if (segment.isAssigned && fromSegment.isAssigned) {
            segment.intersect(fromSegment)
        } else if (segment.isAssigned && !fromSegment.isAssigned) {
            segment.removeChars(fromDigit.chars)
        } else {
            segment
        }
    }
}

fun EncodedDigit.reduceFrom(encodedDigits: List<EncodedDigit>): EncodedDigit {
    return encodedDigits.fold(this) { acc, digit -> acc.reduceFrom(digit) }
}

fun EncodedDigit.reduceFrom(digitPattern: DigitPattern): EncodedDigit {
    val initialSegmentReduction = this.segments.map { segment ->
        if (segment.isAssigned && !digitPattern.contains(segment.position)) {
            segment.clearChars()
        } else {
            segment
        }
    }

    val fixedCharCount = initialSegmentReduction
        .fold(mutableMapOf<Char, Int>()) { acc, segment ->
            if (segment.isFixed) {
                val char = segment.chars.first()
                val count = acc.getOrDefault(char, 0)
                acc[char] = count + 1
            }
            acc
        }

    val finalSegmentReduction = initialSegmentReduction.map { segment ->
        if (segment.isAmbiguous) {
            val charBias = segment.chars.filter { char ->
                val count = fixedCharCount.getOrDefault(char, 0)
                count == 1
            }
            if (charBias.size == 1) {
                segment.removeChar(charBias.first())
            } else {
                segment
            }
        } else {
            segment
        }
    }

    return this.copy(segments = finalSegmentReduction)
}

fun EncodedDigit.matches(pattern: DigitPattern): Boolean {
    val assignedSegments = this.segments.filter { it.isAssigned }.map { it.position }.toSet()
    val patternOutlineMathed = pattern.positions.all { assignedSegments.contains(it) }

    if (!patternOutlineMathed) {
        return false
    }

    data class CharCounter(
        var total: Int = 0,
        var ambiguous: Int = 0
    )

    val filteredSegments = this.segments.filter { pattern.contains(it.position) }

    val charCounters = mutableMapOf<Char, CharCounter>()
    for (segment in filteredSegments) {
        for (char in segment.chars) {
            val counter = charCounters[char] ?: CharCounter()
            if (segment.isAmbiguous) {
                counter.ambiguous += 1
            }
            counter.total += 1
            charCounters[char] = counter
        }
    }

    return charCounters.all { (_, counter) ->
        counter.total - counter.ambiguous <= 1
    }
}

fun EncodedDigit.findMatchingPatterns(vararg patterns: DigitPattern): List<DigitPattern> {
    return patterns.filter { this.matches(it) }
}

fun EncodedDigit.tryDecoding(): Pair<Boolean, DigitPattern?> {
    val patterns = when (chars.size) {
        2 -> arrayOf(DIGIT_PATTERN_1)
        3 -> arrayOf(DIGIT_PATTERN_7)
        4 -> arrayOf(DIGIT_PATTERN_4)
        5 -> FIVE_SEGMENT_DIGIT_PATTERNS
        6 -> SIX_SEGMENT_DIGIT_PATTERNS
        7 -> arrayOf(DIGIT_PATTERN_8)
        else -> error("Encoded digit has invalid size")
    }

    val matchingPatterns = findMatchingPatterns(*patterns)

    return if (matchingPatterns.size == 1) {
        Pair(true, matchingPatterns[0])
    } else {
        Pair(false, null)
    }
}

fun EncodedDigit.tryResolving(): Triple<Boolean, EncodedDigit, Int> {
    val (decoded, pattern) = this.tryDecoding()
    return if (decoded) {
        Triple(true, this.reduceFrom(pattern!!), pattern.value)
    } else {
        Triple(false, this, -1)
    }
}

object EncodedDigitFactory {
    fun fromCharSequence(value: List<Char>): EncodedDigit {
        val charSet = value.toSet()

        if (charSet.size != value.size) {
            throw error("Characters are not unique. $value")
        }

        val segments = when (value.size) {
            2 -> DigitSegmentPosition.values().map { pos ->
                if (DIGIT_PATTERN_1.contains(pos)) {
                    EncodedDigitSegment(pos, charSet)
                } else {
                    EncodedDigitSegment(pos)
                }
            }
            3 -> DigitSegmentPosition.values().map { pos ->
                if (DIGIT_PATTERN_7.contains(pos)) {
                    EncodedDigitSegment(pos, charSet)
                } else {
                    EncodedDigitSegment(pos)
                }
            }
            4 -> DigitSegmentPosition.values().map { pos ->
                if (DIGIT_PATTERN_4.contains(pos)) {
                    EncodedDigitSegment(pos, charSet)
                } else {
                    EncodedDigitSegment(pos)
                }
            }
            5, 6, 7 -> DigitSegmentPosition.values().map { EncodedDigitSegment(it, charSet) }
            else -> error("Invalid encoded segmented digit size ${value.size}")
        }
        return EncodedDigit(value, segments)
    }

    fun fromString(value: String): EncodedDigit {
        return this.fromCharSequence(value.toList())
    }

}

object EncodedDigitListFactory {
    fun fromStrings(values: List<String>): List<EncodedDigit> {
        return values.map { EncodedDigitFactory.fromString(it) }
    }
}