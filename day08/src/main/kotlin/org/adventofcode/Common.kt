package org.adventofcode

//
//                    TOP
//                 ----------
//                |          |
//    TOP_LEFT -> |          | <- TOP_RIGHT
//                |          |
//                 ----------  <- MIDDLE
//                |          |
// BOTTOM_LEFT -> |          | <- BOTTOM_RIGHT
//                |          |
//                 ----------
//                   BOTTOM
//

const val DIGIT_SEGMENT_SIZE = 7

enum class DigitSegmentPosition {
    TOP,
    TOP_LEFT,
    TOP_RIGHT,
    MIDDLE,
    BOTTOM,
    BOTTOM_LEFT,
    BOTTOM_RIGHT,
}

data class DigitPattern(val value: Int, val positions: Set<DigitSegmentPosition>)

fun DigitPattern.contains(position: DigitSegmentPosition): Boolean {
    return positions.contains(position)
}

val DIGIT_PATTERN_0 = DigitPattern(0, setOf(
    DigitSegmentPosition.TOP,
    DigitSegmentPosition.TOP_LEFT,
    DigitSegmentPosition.TOP_RIGHT,
    DigitSegmentPosition.BOTTOM_LEFT,
    DigitSegmentPosition.BOTTOM_RIGHT,
    DigitSegmentPosition.BOTTOM,
))
val DIGIT_PATTERN_1 = DigitPattern(1, setOf(
    DigitSegmentPosition.TOP_RIGHT,
    DigitSegmentPosition.BOTTOM_RIGHT,
))
val DIGIT_PATTERN_2 = DigitPattern(2, setOf(
    DigitSegmentPosition.TOP,
    DigitSegmentPosition.TOP_RIGHT,
    DigitSegmentPosition.MIDDLE,
    DigitSegmentPosition.BOTTOM_LEFT,
    DigitSegmentPosition.BOTTOM,
))
val DIGIT_PATTERN_3 = DigitPattern(3, setOf(
    DigitSegmentPosition.TOP,
    DigitSegmentPosition.TOP_RIGHT,
    DigitSegmentPosition.MIDDLE,
    DigitSegmentPosition.BOTTOM_RIGHT,
    DigitSegmentPosition.BOTTOM,
))
val DIGIT_PATTERN_4 = DigitPattern(4, setOf(
    DigitSegmentPosition.TOP_LEFT,
    DigitSegmentPosition.TOP_RIGHT,
    DigitSegmentPosition.MIDDLE,
    DigitSegmentPosition.BOTTOM_RIGHT,
))
val DIGIT_PATTERN_5 = DigitPattern(5, setOf(
    DigitSegmentPosition.TOP,
    DigitSegmentPosition.TOP_LEFT,
    DigitSegmentPosition.MIDDLE,
    DigitSegmentPosition.BOTTOM_RIGHT,
    DigitSegmentPosition.BOTTOM,
))
val DIGIT_PATTERN_6 = DigitPattern(6, setOf(
    DigitSegmentPosition.TOP,
    DigitSegmentPosition.TOP_LEFT,
    DigitSegmentPosition.MIDDLE,
    DigitSegmentPosition.BOTTOM_LEFT,
    DigitSegmentPosition.BOTTOM_RIGHT,
    DigitSegmentPosition.BOTTOM,
))
val DIGIT_PATTERN_7 = DigitPattern(7, setOf(
    DigitSegmentPosition.TOP,
    DigitSegmentPosition.TOP_RIGHT,
    DigitSegmentPosition.BOTTOM_RIGHT,
))
val DIGIT_PATTERN_8 = DigitPattern(8, setOf(
    DigitSegmentPosition.TOP,
    DigitSegmentPosition.TOP_RIGHT,
    DigitSegmentPosition.TOP_LEFT,
    DigitSegmentPosition.MIDDLE,
    DigitSegmentPosition.BOTTOM_LEFT,
    DigitSegmentPosition.BOTTOM_RIGHT,
    DigitSegmentPosition.BOTTOM,
))
val DIGIT_PATTERN_9 = DigitPattern(9, setOf(
    DigitSegmentPosition.TOP,
    DigitSegmentPosition.TOP_RIGHT,
    DigitSegmentPosition.TOP_LEFT,
    DigitSegmentPosition.MIDDLE,
    DigitSegmentPosition.BOTTOM_RIGHT,
    DigitSegmentPosition.BOTTOM,
))

val FIVE_SEGMENT_DIGIT_PATTERNS = arrayOf(
    DIGIT_PATTERN_2,
    DIGIT_PATTERN_3,
    DIGIT_PATTERN_5,
)

val SIX_SEGMENT_DIGIT_PATTERNS = arrayOf(
    DIGIT_PATTERN_0,
    DIGIT_PATTERN_6,
    DIGIT_PATTERN_9
)