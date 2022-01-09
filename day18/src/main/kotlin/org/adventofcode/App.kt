package org.adventofcode

import java.io.File
import kotlin.math.ceil
import kotlin.math.max

interface ExplodeCarry {
    val carry: Int;
}

sealed class ExplodeResult {
    abstract val value: SnailFishNumber;

    data class Nothing(override val value: SnailFishNumber) : ExplodeResult()
    data class Changed(override val value: SnailFishNumber) : ExplodeResult()
    data class Exploded(override val value: SnailFishNumber.Pair) : ExplodeResult() {
        val leftCarryValue: Int get() = when (value.left) {
            is SnailFishNumber.Literal -> value.left.value
            else -> throw error("Invalid left value ${value.left}")
        }
        val rightCarryValue: Int get() = when (value.right) {
            is SnailFishNumber.Literal -> value.right.value
            else -> throw error("Invalid right value ${value.right}")
        }
    }
    data class CarryLeft(override val value: SnailFishNumber, override val carry: Int) : ExplodeCarry, ExplodeResult()
    data class CarryRight(override val value: SnailFishNumber, override val carry: Int): ExplodeCarry, ExplodeResult()
}

sealed class SnailFishNumber {
    abstract val height: Int
    abstract val splittable: Boolean
    abstract fun toCompactString(): String
    abstract fun addToLeft(value: Int): SnailFishNumber
    abstract fun addToRight(value: Int): SnailFishNumber
    abstract fun explode(): ExplodeResult
    abstract fun split(): SnailFishNumber
    abstract fun magnitude(): Long

    data class Literal(val value: Int) : SnailFishNumber() {
        override val height: Int = -1
        override val splittable: Boolean = value >= 10
        override fun toCompactString(): String = value.toString()
        override fun addToLeft(v: Int): SnailFishNumber = Literal(this.value + v)
        override fun addToRight(v: Int): SnailFishNumber = Literal(this.value + v)
        override fun explode(): ExplodeResult = ExplodeResult.Nothing(this)
        override fun split(): SnailFishNumber = if (splittable) {
            val left = value / 2
            val right = ceil(value.toDouble() / 2.0).toInt()
            Pair(left, right)
        } else this
        override fun magnitude(): Long = value.toLong()
    }

    data class Pair(val left: SnailFishNumber, val right: SnailFishNumber) : SnailFishNumber() {
        constructor(left: Int, right: Int) : this(Literal(left), Literal(right))
        constructor(left: Int, right: SnailFishNumber) : this(Literal(left), right)
        constructor(left: SnailFishNumber, right: Int) : this(left, Literal(right))

        override val height: Int = when {
            left is Literal && right is Literal -> 0
            else -> max(left.height, right.height) + 1
        }
        override val splittable: Boolean = left.splittable || right.splittable
        override fun toCompactString(): String = "[${left.toCompactString()},${right.toCompactString()}]"
        override fun addToLeft(value: Int): SnailFishNumber = Pair(left.addToLeft(value), right)
        override fun addToRight(value: Int): SnailFishNumber = Pair(left, right.addToRight(value))
        override fun explode(): ExplodeResult {
            return if (height == 0) {
                ExplodeResult.Exploded(this)
            } else if (left.height >= right.height) {
                when (val result = left.explode()) {
                    is ExplodeResult.Nothing -> ExplodeResult.Nothing(this)
                    is ExplodeResult.Exploded -> {
                        val nextRight = right.addToLeft(result.rightCarryValue)
                        ExplodeResult.CarryLeft(Pair(0, nextRight), carry = result.leftCarryValue)
                    }
                    is ExplodeResult.CarryLeft -> {
                        ExplodeResult.CarryLeft(Pair(result.value, this.right), carry = result.carry)
                    }
                    is ExplodeResult.CarryRight -> {
                        val nextRight = right.addToLeft(result.carry)
                        ExplodeResult.Changed(Pair(result.value, nextRight))
                    }
                    is ExplodeResult.Changed -> {
                        ExplodeResult.Changed(Pair(result.value, this.right))
                    }
                }
            } else {
                when (val result = right.explode()) {
                    is ExplodeResult.Nothing -> ExplodeResult.Nothing(this)
                    is ExplodeResult.Exploded -> {
                        val nextLeft = left.addToRight(result.leftCarryValue)
                        ExplodeResult.CarryRight(Pair(nextLeft, 0), carry = result.rightCarryValue)
                    }
                    is ExplodeResult.CarryRight -> {
                        ExplodeResult.CarryRight(Pair(this.left, result.value), carry = result.carry)
                    }
                    is ExplodeResult.CarryLeft -> {
                        val nextLeft = left.addToRight(result.carry)
                        ExplodeResult.Changed(Pair(nextLeft, result.value))
                    }
                    is ExplodeResult.Changed -> {
                        ExplodeResult.Changed(Pair(this.left, result.value))
                    }
                }
            }
        }
        override fun split(): SnailFishNumber = if (left.splittable) {
            Pair(left.split(), right)
        } else if (right.splittable) {
            Pair(left, right.split())
        } else this
        override fun magnitude(): Long {
            return (3 * left.magnitude()) + (2 * right.magnitude())
        }
    }
}

fun SnailFishNumber.reduce(): SnailFishNumber {
    return generateSequence(this) { value ->
        if (value.height >= 4) {
            val result = value.explode()
            result.value
        } else if (value.splittable) {
            value.split()
        } else null
    }.last()
}

operator fun Int.plus(value: SnailFishNumber): SnailFishNumber {
    return SnailFishNumber.Pair(SnailFishNumber.Literal(this), value)
}

operator fun SnailFishNumber.plus(value: SnailFishNumber): SnailFishNumber {
    return SnailFishNumber.Pair(this, value)
}

operator fun SnailFishNumber.plus(value: Int): SnailFishNumber {
    return SnailFishNumber.Pair(this, SnailFishNumber.Literal(value))
}

typealias SnailFishNumberList = List<SnailFishNumber>

fun SnailFishNumberList.sum(): SnailFishNumber {
    return this.reduce { acc, snailFishNumber -> (acc + snailFishNumber).reduce() }
}

object SnailFishNumberExpressionParser {
    private sealed class ParserStackEntry {
        object LeftBracket : ParserStackEntry()
        data class Value(val value: SnailFishNumber) : ParserStackEntry()
    }

    fun fromFile(pathname: String): SnailFishNumberList {
        return File(pathname).readLines().map { parse(it) }
    }

    fun parse(expressions: List<String>): SnailFishNumberList {
        return expressions.map { parse(it) }
    }

    fun parse(expression: String): SnailFishNumber {
        // [[[9,2],[[2,9],0]],[1,[[2,3],0]]]
        // [[*,[[2,9],0]],[1,[[2,3],0]]]
        // [[*,[*,0]],[1,[[2,3],0]]]
        // [[*,*],[1,[[2,3],0]]]
        // [*,[1,[[2,3],0]]]
        // [*,[1,[*,0]]]
        // [*,[1,*]]
        // [*,*]
        // *
        var stack = listOf<ParserStackEntry>()
        for (ch in expression) {
            when {
                ch == '[' -> stack += ParserStackEntry.LeftBracket
                ch == ']' -> {
                    val (_, leftEntry, rightEntry) = stack.takeLast(3)
                    val (leftNumber) = when (leftEntry) {
                        is ParserStackEntry.Value -> leftEntry
                        else -> throw error("Invalid left stack entry. stack = $stack")
                    }
                    val (rightNumber) = when (rightEntry) {
                        is ParserStackEntry.Value -> rightEntry
                        else -> throw error("Invalid right stack entry. stack = $stack")
                    }
                    val pair = SnailFishNumber.Pair(leftNumber, rightNumber)
                    stack = stack.dropLast(3)
                    stack = stack + ParserStackEntry.Value(pair)
                }
                ch.isDigit() -> {
                    stack = stack + ParserStackEntry.Value(SnailFishNumber.Literal(ch.digitToInt()))
                }
            }
        }

        if (stack.size != 1) {
            throw error("Invalid parse. stack = $stack")
        }

        return when (val entry = stack.first()) {
            is ParserStackEntry.Value -> entry.value
            else -> throw error("Invalid parse. Remaining stack entry should be a value $stack")
        }
    }
}

typealias SnailFishNumberExpression = String
typealias SnailFishNumberExpressions = List<String>

fun SnailFishNumberExpression.parse(): SnailFishNumber {
    return SnailFishNumberExpressionParser.parse(this)
}

fun SnailFishNumberExpressions.parse(): List<SnailFishNumber> {
    return SnailFishNumberExpressionParser.parse(this)
}

fun runSolutionPart1(numbers: SnailFishNumberList) {
    println("Day 18 Solution: Part 1")
    val total = numbers.sum()
    println("total: ${total.toCompactString()}")
    println("magnitude: ${total.magnitude()}")
}

fun main() {
    val numbers = SnailFishNumberExpressionParser.fromFile("day18/src/main/resources/puzzleInput.txt")
    runSolutionPart1(numbers)
}
