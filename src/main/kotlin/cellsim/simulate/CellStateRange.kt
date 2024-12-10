package cellsim.simulate

import cellsim.misc.ImmutableArrayList
import cellsim.misc.nValuesStr
import cellsim.misc.nthOrdinalStr

class CellStateRange(vararg range1: Int) {
    private var range: ImmutableArrayList<Int>
    init {
        range = ImmutableArrayList(ArrayList(range1.asList()))
    }
    fun getRange(): List<Int> = range

    override fun hashCode() = range.hashCode()
    override fun equals(other: Any?): Boolean {
        if (other !is CellStateRange) return false
        if (range === other.range) return true
        if (range != other.range) return false

        range = other.range
        return true
    }

    fun makeCellState(vararg values: Int): CellState {
        if (values.size != range.size) throw IllegalArgumentException(
            "Expected " + nValuesStr(range.size) + ", got ${values.size}")

        for (i in values.indices) {
            if (values[i] >= range[i] || values[i] < 0) throw IllegalArgumentException(
                nthOrdinalStr(i + 1) + " (1-indexed) cell state value must be non-negative" +
                "and less than ${range[i]}, is ${values[i]}"
            )
        }

        return CellState(this, *values)
    }

}