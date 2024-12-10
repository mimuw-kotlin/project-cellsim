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
class GameOfLifeRGBY {
    companion object {
        fun factory(): Automaton.Factory {
            val factory = Automaton.Factory()
            factory.stateRange = CellStateRange(5) // 0 is dead, 1-4 are colors
            factory.neighborhood = MutableNeighborhood().addRect(-1, 1, -1, 1)
            factory.defaultCellState = DefaultCellStateGenerator{ it.stateRange.makeCellState(0) }
            factory.nextStateFunc = NextCellStateCalculator{
                var alives = 0
                val ofColor = arrayOf(0, 0, 0, 0)

                for (dx in -1..1) {
                    for (dy in -1..1) {
                        if ((dx != 0 || dy != 0) && it.getCell(dx, dy)[0] > 0) {
                            alives++
                            ofColor[it.getCell(dx, dy)[0] - 1]++
                        }
                    }
                }

                var newState = -1
                if (it.thisCell()[0] > 0 && (alives == 2 || alives == 3)) {
                    newState = it.thisCell()[0]
                } else if (it.thisCell()[0] == 0 && alives == 3) {
                    for (i in ofColor.indices) {
                        if (ofColor[i] >= 2) {
                            newState = i + 1
                        }
                    }

                    if (newState == -1) {
                        for (i in ofColor.indices) {
                            if (ofColor[i] == 0) {
                                newState = i + 1
                            }
                        }
                    }
                } else {
                    newState = 0
                }

                it.stateRange.makeCellState(newState)
            }

            return factory
        }

        fun automaton() = factory().create()

        val displayRGBY = ConsoleCellPrinter{
            "\u001b[" + when(it[0]) {
                0 -> "40"
                1 -> "41"
                2 -> "42"
                3 -> "44"
                4 -> "43"
                else -> IllegalArgumentException()
            } + "m   \u001b[m"
        }
    }

}
