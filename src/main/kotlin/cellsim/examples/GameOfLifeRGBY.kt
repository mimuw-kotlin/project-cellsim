package main.kotlin.cellsim.examples

import cellsim.simulate.Automaton
import cellsim.simulate.CellStateRange
import cellsim.simulate.ConsoleCellPrinter
import cellsim.simulate.DefaultCellStateGenerator
import cellsim.simulate.MutableNeighborhood
import cellsim.simulate.NextCellStateCalculator

// Variant of standard Game of Life, where cells have assigned color at birth.
// When two or more "parents" have the same color, "child" is born with this color.
// When all 3 "parents" have different colors, the "child" is born with color unused by "parents".
object GameOfLifeRGBY {
    val stateRange = CellStateRange(5)
    val DEAD = stateRange.nameSubState(0, 4, "Dead")
    val RED = stateRange.nameSubState(0, 0, "Red")
    val GREEN = stateRange.nameSubState(0, 1, "Green")
    val BLUE = stateRange.nameSubState(0, 2, "Blue")
    val YELLOW = stateRange.nameSubState(0, 3, "Yellow")

    fun factory(): Automaton.Factory {
        val factory = Automaton.Factory()
        factory.stateRange = stateRange // 4 is dead, 0-3 are colors
        factory.neighborhood = MutableNeighborhood().addRect(-1, 1, -1, 1)
        factory.defaultCellState = DefaultCellStateGenerator{ it.stateRange.makeCellState(DEAD) }
        factory.nextStateFunc = NextCellStateCalculator{
            var alives = 0
            val ofColor = arrayOf(0, 0, 0, 0)

            for (dx in -1..1) {
                for (dy in -1..1) {
                    if ((dx != 0 || dy != 0) && !(it.getCell(dx, dy) has DEAD)) {
                        alives++
                        ofColor[it.getCell(dx, dy)[0]]++
                    }
                }
            }

            var newState = -1
            if (!(it.thisCell() has DEAD) && (alives == 2 || alives == 3)) { // Cell stays alive
                // Keep the same color:
                newState = it.thisCell()[0]
            } else if (it.thisCell() has DEAD && alives == 3) { // Cell becomes alive
                // Try to set the color which has majority:
                for (i in ofColor.indices) {
                    if (ofColor[i] >= 2) {
                        newState = i
                    }
                }

                // If no color has majority set color which is not present in neighborhood:
                if (newState == -1) {
                    for (i in ofColor.indices) {
                        if (ofColor[i] == 0) {
                            newState = i
                        }
                    }
                }
            } else { // Cell dies
                newState = DEAD.value
            }

            it.stateRange.makeCellState(newState)
        }

        return factory
    }

    fun automaton() = factory().create()

    val displayRGBY = ConsoleCellPrinter{
        "\u001b[" + when {
            it has DEAD -> "40"
            it has RED -> "41"
            it has GREEN -> "42"
            it has BLUE -> "44"
            it has YELLOW -> "43"
            else -> IllegalArgumentException()
        } + "m   \u001b[m"
    }
}
