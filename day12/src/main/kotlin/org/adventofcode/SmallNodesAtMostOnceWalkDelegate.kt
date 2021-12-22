package org.adventofcode

object SmallNodesAtMostOnceWalkDelegate : GraphWalkDelegate<Set<Node>> {
    override fun initialWalkState(): Set<Node> {
        return setOf()
    }

    override fun shouldWalkToNode(node: Node, walkState: Set<Node>): Boolean {
        return node !in walkState
    }

    override fun didWalkToNode(node: Node, path: Path, walkState: Set<Node>): Set<Node> {
        return when (node) {
            is Node.Small -> walkState + node
            else -> walkState
        }
    }
}