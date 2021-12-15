package org.adventofcode

data class DecodingDigit(
    val segments: Map<DigitSegmentPosition, Char> = mapOf()
)

fun DecodingDigit.contains(position: DigitSegmentPosition): Boolean {
    return segments.contains(position)
}

fun DecodingDigit.assignCharToSegment(char: Char, position: DigitSegmentPosition): DecodingDigit {
    return this.copy(segments = segments.plus((position to char)))
}

fun DecodingDigit.resolveFor(encodedDigit: EncodedDigit): DecodingDigit {
    return encodedDigit.resolvedSegments().fold(this) { decodingDigit, segment ->
        decodingDigit.assignCharToSegment(segment.chars.first(), segment.position)
    }
}

fun DecodingDigit.resolveFor(encodedDigits: List<EncodedDigit>): DecodingDigit {
    return encodedDigits.fold(this) { decodingDigit, encodedDigit ->
        decodingDigit.resolveFor(encodedDigit)
    }
}

fun DecodingDigit.complete(): Boolean {
    val positions = this.segments.keys.toSet()
    val chars = this.segments.values.toSet()
    return positions.size == DIGIT_SEGMENT_SIZE && chars.size == DIGIT_SEGMENT_SIZE
}

fun DecodingDigit.applyTo(encodedDigit: EncodedDigit, strict: Boolean = true): EncodedDigit {
    return encodedDigit.mapSegments { segment ->
        val char = this.segments[segment.position]
        if (char != null && segment.chars.contains(char!!)) {
            segment.assignChar(char!!)
        } else if (strict) {
            segment.clearChars()
        } else {
            segment
        }
    }
}

fun DecodingDigit.applyTo(encodedDigits: List<EncodedDigit>): List<EncodedDigit> {
    return encodedDigits.map { this.applyTo(it)}
}