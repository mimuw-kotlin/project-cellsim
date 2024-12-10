package cellsim.misc

import java.io.Serializable

// Copy of standard Kotlin Pair class, but with single type and renamed values
data class PairXY<out T>(
    val x: T,
    val y: T
) : Serializable {
    override fun toString(): String = "($x, $y)"
}