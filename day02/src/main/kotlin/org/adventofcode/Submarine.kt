package org.adventofcode

enum class SubmarineActionType(val value: String) {
    FORWARD("forward"),
    DOWN("down"),
    UP("up"),
}

data class SubmarineCommand(
    val action: SubmarineActionType,
    val steps: Int,
)

data class SubmarineLocation(
    val position: Int = 0,
    val depth: Int = 0,
) {
    val totalArea: Int
        get() = this.depth * this.position
}

fun SubmarineLocation.moveForward (steps: Int): SubmarineLocation {
    return this.copy(position = this.position + steps)
}

fun SubmarineLocation.moveUp (steps: Int): SubmarineLocation {
    val newDepth = this.depth - steps
    return this.copy(depth = if (newDepth < 0) 0 else newDepth)
}

fun SubmarineLocation.moveDown (steps: Int): SubmarineLocation {
    return this.copy(depth = this.depth + steps)
}

fun SubmarineLocation.moveBy (commands: List<SubmarineCommand>): SubmarineLocation {
    return commands.fold(this) { location, (action, steps) ->
        when (action) {
            SubmarineActionType.FORWARD -> location.moveForward(steps)
            SubmarineActionType.UP -> location.moveUp(steps)
            SubmarineActionType.DOWN -> location.moveDown(steps)
            else -> throw Exception("Invalid command action $action")
        }
    }
}