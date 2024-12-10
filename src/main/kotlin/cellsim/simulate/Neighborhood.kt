package cellsim.simulate

import cellsim.misc.PairXY
import cellsim.misc.checkRectCoordinates

// Describes neighborhood of a cell, which is a set of cells relative to each cell
// which can influence that cell ((0, 0) is always in neighborhood)
open class Neighborhood(protected val range: MutableSet<PairXY<Long>>) {
    fun immutableCopy(): ImmutableNeighborhood {
        val copied = ImmutableNeighborhood(range.toMutableSet())
        return copied
    }

    fun contains(x: Long, y: Long) = range.contains(PairXY(x, y))
}

class MutableNeighborhood : Neighborhood(mutableSetOf(PairXY(0, 0))) {
    fun addPoint(x: Long, y: Long): MutableNeighborhood {
        range.add(PairXY(x, y))
        return this
    }

    fun removePoint(x: Long, y: Long): MutableNeighborhood {
        if (x != 0L || y != 0L) range.remove(PairXY(x, y))
        return this
    }

    fun addRect(x1: Long, x2: Long, y1: Long, y2: Long): MutableNeighborhood {
        checkRectCoordinates(x1, x2, y1, y2)

        for (x in x1..x2) {
            for (y in y1..y2) {
                range.add(PairXY(x, y))
            }
        }

        return this
    }

    fun removeRect(x1: Long, x2: Long, y1: Long, y2: Long): MutableNeighborhood {
        checkRectCoordinates(x1, x2, y1, y2)

        for (x in x1..x2) {
            for (y in y1..y2) {
                if (x != 0L || y != 0L) range.remove(PairXY(x, y))
            }
        }

        return this
    }

}

class ImmutableNeighborhood internal constructor(private val set: MutableSet<PairXY<Long>>): Neighborhood(set) {
    fun iterator() = set.iterator()
}
