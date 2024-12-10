package main.kotlin.cellsim.examples

import cellsim.simulate.Automaton
import cellsim.simulate.CellStateRange
import cellsim.simulate.ConsoleCellPrinter
import cellsim.simulate.DefaultCellStateGenerator
import cellsim.simulate.MutableNeighborhood
import cellsim.simulate.NextCellStateCalculator
import kotlin.math.max
import kotlin.math.min

// Variant of Game of Life, where each cell has a lifespan and dies regardless of neighbors.
// Dying of old age can be disabled.
class GameOfLifeFading {
    companion object {
        fun factory(lifespan: Int, dyingOfOldAge: Boolean = true): Automaton.Factory {
            if (lifespan < 1) throw IllegalArgumentException("Lifespan nut be at least 1, is $lifespan")

            val factory = Automaton.Factory()
            factory.stateRange = CellStateRange(lifespan + 1) // 0 is dead, 1 and above means time left to live
            factory.neighborhood = MutableNeighborhood().addRect(-1, 1, -1, 1)
            factory.defaultCellState = DefaultCellStateGenerator{ it.stateRange.makeCellState(0) }
            factory.nextStateFunc = NextCellStateCalculator{
                var alives = 0

                for (dx in -1..1) {
                    for (dy in -1..1) {
                        if ((dx != 0 || dy != 0) && it.getCell(dx, dy)[0] > 0) {
                            alives++
                        }
                    }
                }

                var newState = -1
                newState = if (it.thisCell()[0] > 0 && (alives == 2 || alives == 3)) {
                    max(it.thisCell()[0] - 1, if (dyingOfOldAge) 0 else 1)
                } else if (it.thisCell()[0] == 0 && alives == 3) {
                    lifespan
                } else {
                    0
                }

                it.stateRange.makeCellState(newState)
            }

            return factory
        }

        fun automaton(lifespan: Int, dyingOfOldAge: Boolean = true) = factory(lifespan, dyingOfOldAge).create()

        fun displayFading(lifespan: Int) = ConsoleCellPrinter{
            val color = if (it[0] > 0) (127 + (128 * it[0]) / lifespan) else 0
            "\u001b[48;2;${color};${color};${color}m   \u001b[m"
        }
    }
}