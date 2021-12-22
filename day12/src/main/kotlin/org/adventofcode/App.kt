package org.adventofcode

import java.io.File

sealed class Node {

    abstract infix fun compareTo(other: Node): Int

    companion object {
        fun fromString(s: String): Node = when {
            s == "start" -> Node.Start
            s == "end" -> Node.End
            s.matches("""[A-Z]+""".toRegex()) -> Node.Large(s)
            s.matches("""[a-z]+""".toRegex()) -> Node.Small(s)
            else -> error("Invalid node value $s")
        }
    }

    object Start : Node() {
        override fun toString(): String = "Node.Start"
        override infix fun compareTo(other: Node): Int = when (other) {
            Start -> 0
            else -> 1
        }
    }

    object End : Node() {
        override fun toString(): String = "Node.End"
        override infix fun compareTo(other: Node): Int = when (other) {
            End -> 0
            else -> -1
        }
    }

    data class Large(val value: String) : Node() {
        override infix fun compareTo(other: Node): Int = when (other) {
            Start -> -1
            End -> 1
            is Large -> this.value.compareTo(other.value) * -1
            is Small -> 1
        }
    }

    data class Small(val value: String) : Node() {
        override infix fun compareTo(other: Node): Int = when (other) {
            Start -> -1
            End -> 1
            is Large -> -1
            is Small -> this.value.compareTo(other.value) * -1
        }
    }

}

data class Edge(val first: Node, val second: Node) {
    constructor(pair: Pair<Node, Node>) : this(pair.first, pair.second)
}

typealias EdgeList = List<Edge>

operator fun Edge.contains(n: Node): Boolean {
    return n == first || n == second
}

fun Edge.nodePairingOrNull(n: Node): Node? {
    return when(n) {
        first -> second
        second -> first
        else -> null
    }
}

fun Edge.toNodeList(): List<Node> {
    return listOf(first, second)
}

fun EdgeList.toNodeMap(): Map<Node, Set<Node>> {
    return this.fold<Edge, Map<Node, Set<Node>>>(mapOf()) { mapping, edge ->
        mapping + edge.toNodeList().map { n1 ->
            val n1connections = edge.nodePairingOrNull(n1)?.let { n2 ->
                val set = mapping.getOrElse(n1) { setOf() }
                when (n2) {
                    Node.Start -> set
                    else -> set + n2
                }
            } ?: setOf()
            n1 to n1connections
        }
    }.filter { (key) -> key != Node.End }
}

typealias Path = List<Node>

fun Path.toCompactString(): String {
    return this.joinToString(",") {
        when (it) {
            Node.Start -> "start"
            Node.End -> "end"
            is Node.Small -> it.value
            is Node.Large -> it.value
        }
    }
}

infix fun Path.compareTo(other: Path): Int {
    return when {
        this.isEmpty() && other.isEmpty() -> 0
        this.isEmpty() && other.isNotEmpty() -> 1
        this.isNotEmpty() && other.isEmpty() -> -1
        else -> {
            when (val comparedResult = this.first() compareTo other.first()) {
                0 -> this.drop(1) compareTo other.drop(1)
                else -> comparedResult
            }
        }
    }
}

data class Graph(val edges: List<Edge>)

fun Graph.walk(
    focusNode: Node,
    connections: Map<Node, Set<Node>>,
    path: Path = listOf(),
    smallNodesWalked: Set<Node> = setOf(),
): List<Path> {
    if (focusNode == Node.End) {
        return listOf(path + focusNode)
    }

    val nodesToWalk = connections[focusNode] ?: setOf()
    val nextSmallNodesWalked = when (focusNode) {
        is Node.Small -> smallNodesWalked + focusNode
        else -> smallNodesWalked
    }
    val walkedPath: Path = path + focusNode

    return nodesToWalk.mapNotNull { toNode ->
        when (toNode) {
            in nextSmallNodesWalked -> null
            else -> walk(toNode, connections, walkedPath, nextSmallNodesWalked)
        }
    }.flatten()
}

fun Graph.findAllCompletePaths(): List<Path> {
    return walk(Node.Start, edges.toNodeMap())
}

object EdgeListFactory {
    fun fromFile(pathname: String): EdgeList {
        return File(pathname)
            .readLines()
            .map { line ->
                val (n1value, n2value) = line
                    .split('-')
                    .zipWithNext().first()
                val n1 = Node.fromString(n1value)
                val n2 = Node.fromString(n2value)
                Edge(n1, n2)
            }
    }
}

fun main() {
    val edges = EdgeListFactory.fromFile("day12/src/main/resources/puzzleInput.txt")
    val graph = Graph(edges)
    val paths = graph.findAllCompletePaths()

    paths
        .sortedWith { p1, p2 -> p1 compareTo p2 }
        .reversed()
        .forEach { println(it.toCompactString()) }

    println()
    println("# paths: ${paths.size}")
}