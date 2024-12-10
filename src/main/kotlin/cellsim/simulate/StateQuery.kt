package cellsim.simulate

open class StateQuery protected constructor(protected val automaton: Automaton) {
    var stateRange = automaton.stateRange
}

class DefaultStateQuery internal constructor(
    automaton: Automaton,
    val x: Long,
    val y: Long
): StateQuery(automaton)

class NextStateQuery internal constructor(
    automaton: Automaton,
    private val myX: Long,
    private val myY: Long,
): StateQuery(automaton) {
    // Returns copy of cell state with position relative to your current cell position
    fun getCell(dx: Long, dy: Long): CellState {
        if (!automaton.neighborhood.contains(dx, dy)) throw IllegalArgumentException(
            "dx=$dx, dy=$dy is out of automaton neighborhood"
        )

        return automaton.cellAt(myX + dx, myY + dy).copy()
    }
    fun getCell(dx: Int, dy: Int) = getCell(dx.toLong(), dy.toLong())
    fun thisCell() = getCell(0L, 0L)
}