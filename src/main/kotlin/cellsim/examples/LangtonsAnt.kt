package main.kotlin.cellsim.examples

import cellsim.simulate.Automaton
import cellsim.simulate.CellStateRange
import cellsim.simulate.ConsoleCellPrinter
import cellsim.simulate.DefaultCellStateGenerator
import cellsim.simulate.MutableNeighborhood
import cellsim.simulate.NextCellStateCalculator

// Langton's ant simulator for single ant
object LangtonsAnt {
    val stateRange = CellStateRange(2, 5)
    val BLACK = stateRange.nameSubState(0, 0, "Dead")
    val WHITE = stateRange.nameSubState(0, 1, "White")
    val NO_ANT = stateRange.nameSubState(1, 0, "No ant")
    val NORTH = stateRange.nameSubState(1, 1, "North")
    val EAST = stateRange.nameSubState(1, 2, "East")
    val SOUTH = stateRange.nameSubState(1, 3, "South")
    val WEST = stateRange.nameSubState(1, 4, "West")

    fun factory(): Automaton.Factory {
        val factory = Automaton.Factory()

        factory.stateRange = stateRange

        factory.neighborhood = MutableNeighborhood().addPoint(-1, 0).addPoint(1, 0)
            .addPoint(0, -1).addPoint(0, 1)

        factory.defaultCellState = DefaultCellStateGenerator{ it.stateRange.makeCellState(BLACK, NO_ANT) }

        factory.nextStateFunc = NextCellStateCalculator{
            fun antNewDir(dx: Int, dy: Int): CellStateRange.CellSubState {
                val cell = it.getCell(dx, dy)
                if (cell has NO_ANT) return NO_ANT

                if (cell has BLACK && cell has NORTH) return EAST
                if (cell has WHITE && cell has NORTH) return WEST
                if (cell has BLACK && cell has EAST)  return SOUTH
                if (cell has WHITE && cell has EAST)  return NORTH
                if (cell has BLACK && cell has SOUTH) return WEST
                if (cell has WHITE && cell has SOUTH) return EAST
                if (cell has BLACK && cell has WEST)  return NORTH
                /*if (cell has WHITE && cell has WEST)*/ return SOUTH
                /*return if (cell[0] == 0) (1 + ((cell[1] - 1) + 1) % 4)
                    else (1 + ((cell[1] - 1) + 3) % 4)*/
            }

            var newColor = if (it.thisCell() has BLACK) BLACK else WHITE
            var newAnt = NO_ANT

            if (!(it.thisCell() has NO_ANT)) { // has ant
                newColor = if (it.thisCell() has BLACK) WHITE else BLACK
            } else if (antNewDir(0, -1) == NORTH) { // from south
                newAnt = NORTH
            } else if (antNewDir(-1, 0) == EAST) { // from west
                newAnt = EAST
            } else if (antNewDir(0, 1) == SOUTH) { // from north
                newAnt = SOUTH
            } else if (antNewDir(1, 0) == WEST) { // from east
                newAnt = WEST
            }

            it.stateRange.makeCellState(newColor, newAnt)
        }

        return factory
    }

    fun automaton() = factory().create()

    val displayAnt = ConsoleCellPrinter{
        val symbol = when {
            it has NO_ANT -> " "
            it has NORTH -> "N"
            it has EAST -> "E"
            it has SOUTH -> "S"
            it has WEST -> "W"
            else -> throw IllegalArgumentException()
        }
        "\u001b[31;${if (it has BLACK) 40 else 47}m ${symbol} \u001b[m"
    }
}