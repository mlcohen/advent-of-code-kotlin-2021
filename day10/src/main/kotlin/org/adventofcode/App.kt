package org.adventofcode

import java.io.File

data class Token(val char: Char, val pos: Int)

sealed class EvaluationResult {
    data class Valid(val _blank: Boolean = true): EvaluationResult()
    data class Corrupt(val token: Token): EvaluationResult()
    data class Incomplete(val token: Token): EvaluationResult()
    data class Invalid(val token: Token): EvaluationResult()
}

val LEFT_BRACKET_SET = "([<{".toSet()
val RIGHT_BRACKET_SET = ")]>}".toSet()
val RIGHT_BRACKET_PAIRING = mapOf(
    ')' to '(',
    ']' to '[',
    '}' to '{',
    '>' to '<',
)
val BRACKET_POINTS = mapOf(
    ')' to 3,
    ']' to 57,
    '}' to 1197,
    '>' to 25137,
)

object ExpressionEvaluator {
    fun evaluateNextToken(pos: Int, tokens: List<Token>, stack: List<Token>): EvaluationResult {
        if (pos == tokens.size) {
            val token = stack.last()
            return when (token.char) {
                in LEFT_BRACKET_SET -> EvaluationResult.Incomplete(token)
                in RIGHT_BRACKET_SET -> EvaluationResult.Corrupt(tokens[pos - 1])
                else -> EvaluationResult.Valid()
            }
        }

        val token = tokens[pos]
        return when (token.char) {
            in LEFT_BRACKET_SET -> {
                evaluateNextToken(pos + 1, tokens, stack + token)
            }
            in RIGHT_BRACKET_SET -> {
                val peek = stack.lastOrNull()
                if (peek?.char != RIGHT_BRACKET_PAIRING[token.char]) {
                    EvaluationResult.Corrupt(token)
                } else {
                    evaluateNextToken(pos + 1, tokens, stack.dropLast(1))
                }
            }
            else -> EvaluationResult.Invalid(token)
        }
    }

    fun evaluate(expr: String): EvaluationResult {
        if (expr.isEmpty()) return EvaluationResult.Valid()
        val stack = listOf<Token>()
        val tokens = expr.toList().mapIndexed { idx, c -> Token(c, idx)}
        return evaluateNextToken(0, tokens, stack)
    }
}

fun runSolutionPart1(expressions: List<String>) {
    val evaluations = expressions.map { it to ExpressionEvaluator.evaluate(it) }

    evaluations
        .filter { (_, eval) -> eval is EvaluationResult.Corrupt }
        .forEach { (expr, result) ->  println("evaluate $expr ... $result") }

    val points = evaluations
        .map { (_, eval) -> eval }
        .filterIsInstance<EvaluationResult.Corrupt>()
        .groupingBy { it.token.char }
        .eachCount()
        .toList()
        .sumOf { (char, count) -> count * BRACKET_POINTS[char]!! }

    println("\nPoints = $points")
}

fun main() {
    val expressions = File("day10/src/main/resources/puzzleInput.txt").readLines()
    runSolutionPart1(expressions)
}
