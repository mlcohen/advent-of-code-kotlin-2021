package org.adventofcode

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