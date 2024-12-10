package main.kotlin.cellsim.examples

import cellsim.simulate.Automaton
import cellsim.simulate.CellStateRange
import cellsim.simulate.ConsoleCellPrinter
import cellsim.simulate.DefaultCellStateGenerator
import cellsim.simulate.MutableNeighborhood
import cellsim.simulate.NextCellStateCalculator

// Langton's ant simulator for single ant
class LangtonsAnt {
    companion object {
        fun factory(): Automaton.Factory {
            val factory = Automaton.Factory()

            // first value is the color of the cell,
            // second is ant state at this cell [none, north, east, south, west]
            factory.stateRange = CellStateRange(2, 5)

            factory.neighborhood = MutableNeighborhood().addPoint(-1, 0).addPoint(1, 0)
                .addPoint(0, -1).addPoint(0, 1)

            factory.defaultCellState = DefaultCellStateGenerator{ it.stateRange.makeCellState(0, 0) }

            factory.nextStateFunc = NextCellStateCalculator{
                fun antNewDir(dx: Int, dy: Int): Int {
                    val cell = it.getCell(dx, dy)
                    if (cell[1] == 0) return 0

                    return if (cell[0] == 0) (1 + ((cell[1] - 1) + 1) % 4)
                        else (1 + ((cell[1] - 1) + 3) % 4)
                }

                var newColor = it.thisCell()[0]
                var newAnt = 0

                if (it.thisCell()[1] > 0) { // has ant
                    newColor = 1 - it.thisCell()[0]
                } else if (antNewDir(0, -1) == 1) { // from south
                    newAnt = 1
                } else if (antNewDir(-1, 0) == 2) { // from west
                    newAnt = 2
                } else if (antNewDir(0, 1) == 3) { // from north
                    newAnt = 3
                } else if (antNewDir(1, 0) == 4) { // from east
                    newAnt = 4
                }

                it.stateRange.makeCellState(newColor, newAnt)
            }

            return factory
        }

        fun automaton() = factory().create()

        val displayAnt = ConsoleCellPrinter{
            val symbol = when(it[1]) {
                0 -> " "
                1 -> "N"
                2 -> "E"
                3 -> "S"
                4 -> "W"
                else -> throw IllegalArgumentException()
            }
            "\u001b[31;${if (it[0] == 0) 40 else 47}m ${symbol} \u001b[m"
        }
    }

}