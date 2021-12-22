package org.adventofcode

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