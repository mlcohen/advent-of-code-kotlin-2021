package org.adventofcode

data class SmallNodeAtMostTwiceWalkDelegateState(
    val smallNodesWalked: Map<Node, Int> = mapOf(),
    val smallNodeEnteredTwice: Node? = null,
) {
    val hasSmallNodeBeenEnteredTwice = smallNodeEnteredTwice != null
}

object SmallNodeAtMostTwiceWalkDelegate : GraphWalkDelegate<SmallNodeAtMostTwiceWalkDelegateState> {
    override fun initialWalkState(): SmallNodeAtMostTwiceWalkDelegateState {
        return SmallNodeAtMostTwiceWalkDelegateState()
    }

    override fun shouldWalkToNode(
        node: Node,
        walkState: SmallNodeAtMostTwiceWalkDelegateState,
    ): Boolean {
        return when (node) {
            is Node.Small -> {
                val nodeWalkedCount = walkState.smallNodesWalked[node] ?: 0
                when (nodeWalkedCount) {
                    0 -> true
                    1 -> !walkState.hasSmallNodeBeenEnteredTwice
                    else -> false
                }
            }
            else -> true
        }
    }

    override fun didWalkToNode(
        node: Node,
        path: Path,
        walkState: SmallNodeAtMostTwiceWalkDelegateState,
    ): SmallNodeAtMostTwiceWalkDelegateState {
        return when (node) {
            is Node.Small -> handleDidWalkToSmallNode(node, path, walkState)
            else -> walkState
        }
    }

    private fun handleDidWalkToSmallNode(
        node: Node,
        path: Path,
        walkState: SmallNodeAtMostTwiceWalkDelegateState,
    ): SmallNodeAtMostTwiceWalkDelegateState {
        val nodeWalkedCount = walkState.smallNodesWalked[node] ?: 0

        if (walkState.hasSmallNodeBeenEnteredTwice && nodeWalkedCount == 1) {
            throw error("Invalid walk $node. ${walkState.smallNodeEnteredTwice} has already been entered twice")
        }

        val nextNodeWalkCount = nodeWalkedCount + 1
        val nextSmallNodesWalked = walkState.smallNodesWalked + Pair(node, nextNodeWalkCount)

        val nextSmallNodeEnteredTwice = when {
            walkState.hasSmallNodeBeenEnteredTwice -> walkState.smallNodeEnteredTwice
            nextNodeWalkCount == 1 -> null
            nextNodeWalkCount == 2 -> node
            else -> error("Invalid walk count")
        }

        return SmallNodeAtMostTwiceWalkDelegateState(
            smallNodesWalked = nextSmallNodesWalked,
            smallNodeEnteredTwice = nextSmallNodeEnteredTwice,
        )
    }
}