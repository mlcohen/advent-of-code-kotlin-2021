package org.adventofcode

import java.io.File

data class Dot(val col: Int, val row: Int)
sealed class FoldAction {
    data class FoldLeft(val fromColumn: Int) : FoldAction()
    data class FoldUp(val fromRow: Int) : FoldAction()
}

typealias DotSet = Set<Dot>

fun DotSet.width(): Int {
    return if (this.isEmpty()) 0 else this.maxOf { it.col } + 1
}

fun DotSet.height(): Int {
    return if (this.isEmpty()) 0 else this.maxOf { it.row } + 1
}

fun DotSet.splitByRow(row: Int): Pair<DotSet, DotSet> {
    val initState = setOf<Dot>() to setOf<Dot>()
    return this.fold(initState) { (aboveRowGroup, belowRowGroup), dot ->
        when {
            dot.row < row -> aboveRowGroup + dot to belowRowGroup
            dot.row > row -> aboveRowGroup to belowRowGroup + dot
            else -> error("Invalid condition $row $dot")
        }
    }
}

fun DotSet.splitByColumn(col: Int): Pair<DotSet, DotSet> {
    val initState = setOf<Dot>() to setOf<Dot>()
    return this.fold(initState) { (leftColumnGroup, rightColumnGroup), dot ->
        when {
            dot.col < col -> leftColumnGroup + dot to rightColumnGroup
            dot.col > col -> leftColumnGroup to rightColumnGroup + dot
            else -> error("Invalid condition $col $dot")
        }
    }
}

data class Paper(val dots: DotSet, val foldWidth: Int? = null, val foldHeight: Int? = null) {
    val width by lazy { foldWidth?.let { it } ?: dots.width() }
    val height by lazy { foldHeight?.let { it } ?: dots.height() }
}

fun Paper.foldUp(row: Int): Paper {
    val (dotsAboveFold, dotsBelowFold) = this.dots.splitByRow(row)
    val foldedDots = dotsBelowFold.fold(dotsAboveFold.toSet()) { set, dot ->
        val foldedDot = Dot(dot.col, row - (dot.row - row))
        set + foldedDot
    }
    return Paper(foldedDots, this.width, row)
}

fun Paper.foldLeft(col: Int): Paper {
    val (dotsLeftOfFold, dotsRightOfFold) = this.dots.splitByColumn(col)
    val foldedRightDots = dotsRightOfFold.map {
        Dot(it.col - col - 1, it.row)
    }
    val foldedDots = dotsLeftOfFold.fold(foldedRightDots.toSet()) { set, dot ->
        val foldedDot = Dot(col - dot.col - 1, dot.row)
        set + foldedDot
    }
    return Paper(foldedDots, col, this.height)
}

fun Paper.fold(foldAction: FoldAction): Paper {
    return when (foldAction) {
        is FoldAction.FoldUp -> foldUp(foldAction.fromRow)
        is FoldAction.FoldLeft -> foldLeft(foldAction.fromColumn)
    }
}

fun Paper.flipVertically(): Paper {
    val midCol = this.width / 2
    val flippedDots = this.dots.map {
        val col = if (it.col > midCol) {
            midCol - (it.col - midCol)
        } else midCol + (midCol - it.col)
        Dot(col, it.row)
    }
    return this.copy(dots = flippedDots.toSet())
}

fun Paper.prettyPrint(fold: FoldAction? = null) {
    val dotset = this.dots.toSet()
    (0 until height).forEach { row ->
        (0 until width)
            .joinToString("") { col -> when {
                fold is FoldAction.FoldUp && fold.fromRow == row -> "-"
                fold is FoldAction.FoldLeft && fold.fromColumn == col -> "|"
                Dot(col, row) in dotset -> "#"
                else -> "."
            } }
            .also { println(it) }
    }
}

val FOLD_ACTION_REGEX_PATTERN = """fold along (\w)=(\d+)""".toRegex()

object InputParser {

    fun fromFile(pathname: String): Pair<List<Dot>, List<FoldAction>> {
        val rawInput = File(pathname).readLines()
        val groups = mutableListOf(mutableListOf<String>())
        rawInput.forEach { input ->
            if (input.isEmpty()) {
                groups.add(mutableListOf())
            } else {
                groups.last().add(input)
            }
        }
        val dots = groups.first().map {
            val parts = it.split(",").map(String::toInt)
            Dot(parts.first(), parts.last())
        }
        val folds = groups.last().mapNotNull {
            val result = FOLD_ACTION_REGEX_PATTERN.matchEntire(it)
            result?.groupValues?.let { (_, direction, position) -> when (direction) {
                "y" -> FoldAction.FoldUp(position.toInt())
                "x" -> FoldAction.FoldLeft(position.toInt())
                else -> error("Unknown fold direction $direction")
            } }
        }
        return dots to folds
    }

}

fun runSolutionPart1(dots: DotSet, foldActions: List<FoldAction>) {
    println("Day 13 Solution: Part 1\n")

    val paper = Paper(dots)
    val foldedPaper = paper.fold(foldActions.first())

    println("visible dots: ${foldedPaper.dots.size}")
}

fun runSolutionPart2(dots: DotSet, foldActions: List<FoldAction>) {
    println("Day 13 Solution: Part 2\n")

    val initPaper = Paper(dots)
    val foldedPaper = foldActions.fold(initPaper) { paper, action -> paper.fold(action) }
    
    foldedPaper.flipVertically().prettyPrint()
}

fun main() {
    val (dots, foldActions) = InputParser.fromFile("day13/src/main/resources/puzzleInput.txt")
//    runSolutionPart1(dots.toSet(), foldActions)
    runSolutionPart2(dots.toSet(), foldActions)
}
