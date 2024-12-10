package cellsim.simulate

class CellState internal constructor(val range: CellStateRange, vararg state: Int) {
    private val state = ArrayList<Int>(state.asList())

    operator fun get(index: Int) = state[index]

    fun copy() = CellState(range, *state.toIntArray())

    override fun hashCode() = state.hashCode()
    override operator fun equals(other: Any?): Boolean {
        if (other !is CellState) return false

        if (range != other.range) return false
        return state == other.state
    }
}
