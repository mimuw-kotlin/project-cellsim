package cellsim.simulate

import cellsim.misc.InvalidCellStateFunctionReturnValue
import cellsim.misc.PairXY
import cellsim.misc.checkRectCoordinates

fun interface DefaultCellStateGenerator {
    fun getDefaultCellState(q: DefaultStateQuery): CellState
}

fun interface NextCellStateCalculator {
    fun getNextCellState(q: NextStateQuery): CellState
}

fun interface ConsoleCellPrinter {
    fun print(c: CellState): String
}

class Automaton(
    val stateRange: CellStateRange,
    val defaultCellState: DefaultCellStateGenerator,
    val neighborhood: ImmutableNeighborhood,
    val nextStateFunc: NextCellStateCalculator
) {
    private var nonDefaultCells: MutableMap<PairXY<Long>, CellState> = HashMap<PairXY<Long>, CellState>()
    private var checkNextStep: MutableSet<PairXY<Long>> = HashSet<PairXY<Long>>()

    class Factory {
        var stateRange: CellStateRange? = null
        var defaultCellState: DefaultCellStateGenerator? = null
        var neighborhood: Neighborhood? = null
        var nextStateFunc: NextCellStateCalculator? = null

        fun create() = Automaton(stateRange!!, defaultCellState!!,  neighborhood!!.immutableCopy(), nextStateFunc!!)
    }

    fun defaultCellOf(x: Long, y: Long): CellState {
        val cell = defaultCellState.getDefaultCellState(DefaultStateQuery(this, x, y))
        return if (cell.range == stateRange) cell else throw InvalidCellStateFunctionReturnValue()
    }

    fun cellAt(x: Long, y: Long): CellState = nonDefaultCells[PairXY(x, y)] ?: defaultCellOf(x, y)

    // Adds cells which have cell specified by (x, y) in their neighborhood to set
    private fun MutableSet<PairXY<Long>>.queryPossiblyAffectedCells(x: Long, y: Long, nb: ImmutableNeighborhood)  {
        with(nb.iterator()) {
            while (hasNext()) {
                val p = next()
                add(PairXY<Long>(x - p.x, y - p.y))
            }
        }
    }

    // Sets cell state to value at given key, or remove from map if value at key is default cell state.
    // Returns true if function call changed the map
    private fun MutableMap<PairXY<Long>, CellState>.setNonDefault(key: PairXY<Long>, value: CellState): Boolean {
        if (value == (this[key]?: defaultCellOf(key.x, key.y))) return false

        if (value == defaultCellOf(key.x, key.y)) {
            this.remove(key)
        } else {
            this[key] = value
        }
        return true
    }

    fun setCell(x: Long, y: Long, cell: CellState): Boolean {
        if (stateRange != cell.range) throw IllegalArgumentException("Trying to set a cell in automaton " +
                "with invalid state range")

        if (nonDefaultCells.setNonDefault(PairXY(x, y), cell)) {
            checkNextStep.queryPossiblyAffectedCells(x, y, neighborhood)
            return true
        } else {
            return false
        }
    }
    fun setCell(x: Int, y: Int, cell: CellState) = setCell(x.toLong(), y.toLong(), cell)

    fun step() {
        val nextNonDefaultCells = nonDefaultCells.toMutableMap()
        val nextCheckNextStep = HashSet<PairXY<Long>>()

        for (checkedCell in checkNextStep) {
            val nextState = nextStateFunc.getNextCellState(NextStateQuery(this, checkedCell.x, checkedCell.y))

            if (nextNonDefaultCells.setNonDefault(PairXY(checkedCell.x, checkedCell.y), nextState)) {
                nextCheckNextStep.queryPossiblyAffectedCells(checkedCell.x, checkedCell.y, neighborhood)
            }
        }

        nonDefaultCells = nextNonDefaultCells
        checkNextStep = nextCheckNextStep
    }

    fun step(stepCount: Int) {
        for (i in 0..<stepCount) {
            step()
        }
    }


    fun printBoard(x1: Long, x2: Long, y1: Long, y2: Long, ccp: ConsoleCellPrinter) {
        checkRectCoordinates(x1, x2, y1, y2)

        for (y in y2 downTo y1) {
            for (x in x1.. x2) {
                print(ccp.print(cellAt(x, y)))
            }
            println()
        }
    }


}