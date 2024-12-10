package cellsim.misc

class ImmutableArrayList<T>(private val aList: ArrayList<T>): List<T> by aList {
    override fun hashCode() = aList.hashCode()
    override operator fun equals(other: Any?): Boolean {
        if (other !is ImmutableArrayList<T>) return false

        return aList == other.aList
    }
}