package org.adventofcode

interface GraphWalkDelegate<T> {
    fun initialWalkState(): T;
    fun shouldWalkToNode(node: Node, walkState: T): Boolean;
    fun didWalkToNode(node: Node, path: Path, walkState: T): T;
}

class Graph(
    private val edges: List<Edge>,
) {
    private fun <T> walk(
        walkedNode: Node,
        connections: Map<Node, Set<Node>>,
        path: Path = listOf(),
        walkState: T,
        delegate: GraphWalkDelegate<T>,
    ): List<Path> {
        if (walkedNode == Node.End) {
            return listOf(path + walkedNode)
        }

        val nodesToWalk = connections[walkedNode] ?: setOf()
        val walkedPath: Path = path + walkedNode
        val nextWalkState = delegate.didWalkToNode(walkedNode, walkedPath, walkState)

        return nodesToWalk.mapNotNull { toNode ->
            if (delegate.shouldWalkToNode(toNode, nextWalkState)) {
                walk(toNode, connections, walkedPath, nextWalkState, delegate)
            } else {
                null
            }
        }.flatten()
    }

    fun findAllCompletePaths(): List<Path> {
        return walk(Node.Start, edges.toNodeMap(),
            walkState = SmallNodesAtMostOnceWalkDelegate.initialWalkState(),
            delegate = SmallNodesAtMostOnceWalkDelegate,
        )
    }

    fun <T> findAllCompletePaths(
        delegate: GraphWalkDelegate<T>,
    ): List<Path> {
        return walk<T>(Node.Start, edges.toNodeMap(),
            walkState = delegate.initialWalkState(),
            delegate = delegate,
        )
    }
}