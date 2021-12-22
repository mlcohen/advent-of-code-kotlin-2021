package org.adventofcode

fun runSolutionPart1(graph: Graph) {
    println("Day 12: Solution Part 1")
    println()

    val paths = graph.findAllCompletePaths()

    paths
        .sortedWith { p1, p2 -> p1 compareTo p2 }
        .reversed()
        .forEach { println(it.toCompactString()) }

    println()
    println("# paths: ${paths.size}\n")
}

fun runSolutionPart2(graph: Graph) {
    println("Day 12: Solution Part 2")
    println()

    val paths = graph.findAllCompletePaths(SmallNodeAtMostTwiceWalkDelegate)

    paths
        .sortedWith { p1, p2 -> p1 compareTo p2 }
        .reversed()
        .forEach { println(it.toCompactString()) }

    println()
    println("# paths: ${paths.size}\n")
}

fun main() {
    val edges = EdgeListFactory.fromFile("day12/src/main/resources/sampleInput1.txt")
    val graph = Graph(edges)
    runSolutionPart1(graph)
    runSolutionPart2(graph)

}