package org.adventofcode

import java.io.File

data class Token(val char: Char, val pos: Int)

sealed class EvaluationResult {
    data class Valid(val _blank: Boolean = true): EvaluationResult()
    data class Corrupt(val token: Token, val stack: List<Token>): EvaluationResult()
    data class Incomplete(val token: Token, val stack: List<Token>): EvaluationResult()
    data class Invalid(val token: Token): EvaluationResult()
}

data class RepairResult(val completion: String)

val LEFT_BRACKET_SET = "([<{".toSet()
val RIGHT_BRACKET_SET = ")]>}".toSet()
val BRACKET_PAIRINGS = listOf(
    '(' to ')',
    '[' to ']',
    '{' to '}',
    '<' to '>',
)
val LEFT_TO_RIGHT_BRACKET_MAP = BRACKET_PAIRINGS.toMap()
val RIGHT_TO_LEFT_BRACKET_MAP = BRACKET_PAIRINGS.associate { it.second to it.first }
val EVAL_BRACKET_POINTS = mapOf(
    ')' to 3,
    ']' to 57,
    '}' to 1197,
    '>' to 25137,
)
val REPAIR_BRACKET_POINTS = mapOf(
    ')' to 1,
    ']' to 2,
    '}' to 3,
    '>' to 4,
)

object ExpressionEvaluator {
    fun evaluateNextToken(pos: Int, tokens: List<Token>, stack: List<Token>): EvaluationResult {
        if (pos == tokens.size) {
            val token = stack.last()
            return when (token.char) {
                in LEFT_BRACKET_SET -> EvaluationResult.Incomplete(token, stack)
                in RIGHT_BRACKET_SET -> EvaluationResult.Corrupt(tokens[pos - 1], stack)
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
                if (peek?.char != RIGHT_TO_LEFT_BRACKET_MAP[token.char]) {
                    EvaluationResult.Corrupt(token, stack)
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

object IncompleteExpressionRepairer {
    fun buildCompletionString(stack: List<Token>, expr: String = ""): String {
        if (stack.isEmpty()) {
            return expr;
        }

        val token = stack.last()
        val currentExpr = expr + LEFT_TO_RIGHT_BRACKET_MAP[token.char]
        return buildCompletionString(stack.dropLast(1), currentExpr)
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
        .sumOf { (char, count) -> count * EVAL_BRACKET_POINTS[char]!! }

    println("\nPoints = $points")
}

fun runSolutionPart2(expressions: List<String>) {
    val evaluations = expressions.map { it to ExpressionEvaluator.evaluate(it) }

    val repairs = evaluations
        .filter { (_, result) -> result is EvaluationResult.Incomplete }
        .map { (expr, result) ->
            val stack = (result as EvaluationResult.Incomplete).stack
            expr to IncompleteExpressionRepairer.buildCompletionString(stack)
        }

    val sumPointsForRepair: (String) -> Long = { repair: String ->
        repair.toList().fold(0L) { points, bracket ->
            (points * 5) + REPAIR_BRACKET_POINTS[bracket]!!
        }
    }

    println()

    val scoredRepairs = repairs
        .map { (expr, repair) -> repair to sumPointsForRepair(repair) }
        .sortedBy { (_, points) -> points }

    scoredRepairs.forEach { (repair, points) ->
        println("repair $repair: points = $points")
    }

    val middleScoredReport = scoredRepairs[scoredRepairs.size / 2]

    println("\nMiddle: $middleScoredReport")
}

fun main() {
    val expressions = File("day10/src/main/resources/puzzleInput.txt").readLines()
//    runSolutionPart1(expressions)
    runSolutionPart2(expressions)
}
