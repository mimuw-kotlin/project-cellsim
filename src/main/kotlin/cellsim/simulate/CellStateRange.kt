package cellsim.simulate

import cellsim.misc.nValuesStr
import cellsim.misc.nthOrdinalStr

// Class representing discrete set of states, that cell can be in.
// For users convenience (hopefully) it is represented as a tuple.
// For example, Langton's ant uses states
// {BLACK, WHITE} x {NO_ANT, NORTH, EAST, SOUTH, WEST},
// so appropriate state range would be CellStateRange(2, 5)
class CellStateRange(vararg range1: Int) {
    private val range: List<Int>
    init {
        range = ArrayList(range1.asList())
    }
    fun getRange(): List<Int> = range

    private val subStateNames = HashMap<Pair<Int, Int>, String>()

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

    class CellSubState internal constructor (val of: CellStateRange, val at: Int, val value: Int)

    fun extractSubState(at: Int, value: Int): CellSubState {
        if (range[at] <= value) throw IllegalArgumentException(
            nthOrdinalStr(at) + " field has max value of ${range[at] - 1}, trying to make sub-state of value $value"
        )
        return CellSubState(this, at, value)
    }

    fun makeCellState(vararg values: CellSubState): CellState {
        if (values.size != range.size) throw IllegalArgumentException(
            "Expected " + nValuesStr(range.size) + ", got ${values.size}")

        val extractedValues = ArrayList<Int?>(values.size)
        repeat(values.size) {
            extractedValues.add(null)
        }

        for (i in values) {
            if (i.of != this) throw IllegalArgumentException("sub-state comes from different state range")
            if (extractedValues[i.at] != null) throw IllegalArgumentException(
                "Trying to make cell state with ambiguous sub-state"
            )

            extractedValues[i.at] = i.value
        }

        val extractedNonNullValues = ArrayList<Int>(values.size)
        for (i in extractedValues.indices) {
            extractedNonNullValues.add(extractedValues[i]!!)
        }
        return CellState(this, *extractedNonNullValues.toIntArray())
    }

    fun nameSubState(at: Int, value: Int, name: String?): CellSubState {
        if (range[at] <= value) throw IllegalArgumentException("TODO proper message")
        if (name != null) {
            subStateNames[Pair(at, value)] = name
        } else {
            subStateNames.remove(Pair(at, value))
        }

        return CellSubState(this, at, value)
    }

    fun getSubStateName(at: Int, value: Int) = subStateNames[Pair(at, value)]
}

