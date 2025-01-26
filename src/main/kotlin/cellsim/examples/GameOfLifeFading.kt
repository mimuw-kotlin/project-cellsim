package main.kotlin.cellsim.examples

import cellsim.simulate.Automaton
import cellsim.simulate.CellStateRange
import cellsim.simulate.ConsoleCellPrinter
import cellsim.simulate.DefaultCellStateGenerator
import cellsim.simulate.MutableNeighborhood
import cellsim.simulate.NextCellStateCalculator
import kotlin.math.max

// Variant of Game of Life, where each cell has a lifespan and dies regardless of neighbors.
// Dying of old age can be disabled, so cells only fade in color.
class GameOfLifeFading(val lifespan: Int) {
    init {
        if (lifespan < 1) throw IllegalArgumentException("Lifespan must be at least 1, is $lifespan")
    }

    val stateRange = CellStateRange(lifespan + 1) // 0 is dead, 1 and above means time left to live
    val DEAD = stateRange.nameSubState(0, 0, "Dead")
    init {
        for (i in 1..lifespan) {
            stateRange.nameSubState(0, i, "Alive$i")
        }
    }
    fun ALIVE(timeLeft: Int): CellStateRange.CellSubState {
        if (timeLeft < 1 || timeLeft > lifespan) throw IllegalArgumentException(
            "Alive cells must have between 1 and $lifespan time left, trying to make with $timeLeft"
        )

        return stateRange.extractSubState(0, timeLeft)
    }

    fun factory(dyingOfOldAge: Boolean = true): Automaton.Factory {
        val factory = Automaton.Factory()
        factory.stateRange = stateRange
        factory.neighborhood = MutableNeighborhood().addRect(-1, 1, -1, 1)
        factory.defaultCellState = DefaultCellStateGenerator { it.stateRange.makeCellState(DEAD) }
        factory.nextStateFunc = NextCellStateCalculator {
            var alives = 0

            for (dx in -1..1) {
                for (dy in -1..1) {
                    if ((dx != 0 || dy != 0) && !(it.getCell(dx, dy) has DEAD)) {
                        alives++
                    }
                }
            }

            var newState = -1
            newState = if (!(it.thisCell() has DEAD) && (alives == 2 || alives == 3)) {
                max(it.thisCell()[0] - 1, if (dyingOfOldAge) DEAD.value else ALIVE(1).value)
            } else if (it.thisCell()[0] == 0 && alives == 3) {
                lifespan
            } else {
                DEAD.value
            }

            it.stateRange.makeCellState(newState)
        }

        return factory
    }

    fun automaton(dyingOfOldAge: Boolean = true) = factory(dyingOfOldAge).create()

    fun displayFading() = ConsoleCellPrinter {
        val color = if (!(it has DEAD)) (127 + (128 * it[0]) / lifespan) else 0
        "\u001b[48;2;${color};${color};${color}m   \u001b[m"
    }
}