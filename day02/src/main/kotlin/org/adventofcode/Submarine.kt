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

data class SubmarineAimLocation(
    val position: Int = 0,
    val aim: Int = 0,
    val depth: Int = 0,
) {
    val totalArea: Int
        get() = this.depth * this.position
}

fun SubmarineAimLocation.moveForward (steps: Int): SubmarineAimLocation {
    return this.copy(
        position = this.position + steps,
        depth = (this.aim * steps) + this.depth,
    )
}

fun SubmarineAimLocation.aimUp (aim: Int): SubmarineAimLocation {
    val newAim = this.aim - aim
    return this.copy(aim = if (newAim < 0) 0 else newAim)
}

fun SubmarineAimLocation.aimDown (aim: Int): SubmarineAimLocation {
    return this.copy(aim = this.aim + aim)
}

fun SubmarineAimLocation.moveBy (commands: List<SubmarineCommand>): SubmarineAimLocation {
    return commands.fold(this) { location, (action, value) ->
        when (action) {
            SubmarineActionType.FORWARD -> location.moveForward(value)
            SubmarineActionType.UP -> location.aimUp(value)
            SubmarineActionType.DOWN -> location.aimDown(value)
            else -> throw Exception("Invalid command action $action")
        }
    }
}