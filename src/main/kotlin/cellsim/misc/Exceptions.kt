package cellsim.misc

internal fun nValuesStr(n: Int) = "$n value" + (if (n != 1) "s" else "")
internal fun nthOrdinalStr(n: Int) = "$n" + when {
    (n % 100) == 11 || (n % 100) == 12 || (n % 100) == 13 -> "th"
    n % 10 == 1 -> "st"
    n % 10 == 2 -> "nd"
    n % 10 == 3 -> "rd"
    else -> "th"
}

internal fun checkRectCoordinates(x1: Long, x2: Long, y1: Long, y2: Long) =
    if (x1 <= x2 && y1 <= y2) Unit else
        throw IllegalArgumentException("Should be x1 <= x2 && y1 <= y2, is " +
                "x1=$x1, x2=$x2, y1=$y1, y2=$y2")

class InvalidCellStateFunctionReturnValue: Exception()