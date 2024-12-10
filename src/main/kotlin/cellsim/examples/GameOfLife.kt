package main.kotlin.cellsim.examples

import cellsim.misc.PairXY
import cellsim.simulate.Automaton
import cellsim.simulate.CellStateRange
import cellsim.simulate.ConsoleCellPrinter
import cellsim.simulate.DefaultCellStateGenerator
import cellsim.simulate.MutableNeighborhood
import cellsim.simulate.NextCellStateCalculator
import kotlin.collections.listOf

// Conway's Game of Life, including easily customizable rules and a bunch of helper presets
class GameOfLife {
    companion object {
        fun factory(stayAlive: List<Int>, becomeAlive: List<Int>): Automaton.Factory {
            val stay = mutableListOf<Int>()
            val become = mutableListOf<Int>()
            for (lists in listOf(Pair(stayAlive, stay), Pair(becomeAlive, become))) {
                for (v in lists.first) {
                    if (v < 0 || v > 8) throw IllegalArgumentException("CGOL allows 0-8 alive neighbors, but not $v")
                    if (lists.second.contains(v)) throw IllegalArgumentException("Duplicate value ($v) in argument list")
                    lists.second.add(v)
                }
            }

            val factory = Automaton.Factory()
            factory.stateRange = CellStateRange(2)
            factory.neighborhood = MutableNeighborhood().addRect(-1, 1, -1, 1)

            factory.defaultCellState = Presets.defaultAllDead

            factory.nextStateFunc = NextCellStateCalculator{
                var alives = 0
                for (dx in -1..1) {
                    for (dy in -1..1) {
                        if ((dx != 0 || dy != 0) && it.getCell(dx, dy)[0] == 1) alives++
                    }
                }

                val stayOrBecome = if (it.thisCell()[0] == 1) stay else become
                it.stateRange.makeCellState(if (stayOrBecome.contains(alives)) 1 else 0)
            }

            return factory
        }

        fun automaton(stayAlive: List<Int>, becomeAlive: List<Int>) = factory(stayAlive, becomeAlive).create()

        fun factory() = factory(listOf(2, 3), listOf(3))
        fun automaton() = automaton(listOf(2, 3), listOf(3))
    }

    class Presets {
        companion object {
            // Stable default state patterns:
            val defaultAllDead = DefaultCellStateGenerator{ it.stateRange.makeCellState(0) }
            fun defaultSquares(dist: Int) = DefaultCellStateGenerator{
                if (dist < 1) throw IllegalArgumentException("For a pattern to be stable, " +
                        "dist must be at least 1, is $dist")
                fun mod(a: Long, b: Long) = ((a % b + b) % b)
                it.stateRange.makeCellState(if  (mod(it.x, dist + 2L) < 2 && mod(it.y, (dist + 2L)) < 2) 1 else 0)
            }

            // Display functions:
            val displayDotHash = ConsoleCellPrinter{ if (it[0] == 1) "." else "#" }
            val displayHashBlackWhite = ConsoleCellPrinter{
                if (it[0] == 1) "\u001b[37m#\u001b[m" else "\u001b[30m#\u001b[m"
            }
            fun displayBgBlackWhite(spaces: Int) = ConsoleCellPrinter{
                if (spaces < 1) throw IllegalArgumentException("At least 1 space required, is $spaces")
                "\u001b[${if (it[0] == 1) "47" else "40"}m" + " ".repeat(spaces) + "\u001b[m"
            }
            val displayBgBlackWhiteWithGrid = ConsoleCellPrinter{
                "\u001b[${if (it[0] == 1) "47" else "40"}m[]\u001b[m"
            }
        }
    }

    class Patterns {
        companion object {
            // Gliders going down-right:
            val glider0 = listOf(PairXY(0, 1), PairXY(1, 0), PairXY(-1, -1), PairXY(0, -1), PairXY(1, -1))
            val glider1 = listOf(PairXY(-1, 1), PairXY(1, 1), PairXY(0, 0), PairXY(1, 0), PairXY(0, -1))

            // https://conwaylife.com/wiki/Gosper_glider_gun
            val gosperGliderGun = listOf(
                PairXY(7, 4), PairXY(5, 3), PairXY(7, 3), PairXY(-5, 2), PairXY(-4, 2), PairXY(3, 2), PairXY(4, 2),
                PairXY(17, 2), PairXY(18, 2), PairXY(-6, 1), PairXY(-2, 1), PairXY(3, 1), PairXY(4, 1), PairXY(17, 1),
                PairXY(18, 1), PairXY(-17, 0), PairXY(-16, 0), PairXY(-7, 0), PairXY(-1, 0), PairXY(3, 0), PairXY(4, 0),
                PairXY(-17, -1), PairXY(-16, -1), PairXY(-7, -1), PairXY(-3, -1), PairXY(-1, -1), PairXY(0, -1),
                PairXY(5, -1), PairXY(7, -1), PairXY(-7, -2), PairXY(-1, -2), PairXY(7, -2), PairXY(-6, -3),
                PairXY(-2, -3), PairXY(-5, -4), PairXY(-4, -4)
            )
        }
    }
}
