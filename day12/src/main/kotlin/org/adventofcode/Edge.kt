package org.adventofcode

import java.io.File

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