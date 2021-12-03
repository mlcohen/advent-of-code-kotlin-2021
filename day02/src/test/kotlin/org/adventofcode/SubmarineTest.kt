package org.adventofcode

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals

class SubmarineTest {
    @Test fun testSubmarineLocation() {
        val commands = listOf(
            SubmarineCommand(action = SubmarineActionType.FORWARD, steps = 5),
            SubmarineCommand(action = SubmarineActionType.DOWN, steps = 5),
            SubmarineCommand(action = SubmarineActionType.FORWARD, steps = 8),
            SubmarineCommand(action = SubmarineActionType.UP, steps = 3),
            SubmarineCommand(action = SubmarineActionType.DOWN, steps = 8),
            SubmarineCommand(action = SubmarineActionType.FORWARD, steps = 2),
        )
        val loc = SubmarineLocation().moveBy(commands)
        assertEquals(loc, SubmarineLocation(position = 15, depth = 10))
        assertEquals(loc.totalArea, 150)
    }
}