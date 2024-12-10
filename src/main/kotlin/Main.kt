package main.kotlin

import main.kotlin.cellsim.examples.GameOfLife
import main.kotlin.cellsim.examples.GameOfLifeFading
import main.kotlin.cellsim.examples.GameOfLifeRGBY
import main.kotlin.cellsim.examples.LangtonsAnt
import kotlin.math.max
import kotlin.random.Random

fun main(args: Array<String>) {
    //gun()
    //gunRGBY()
    //gunFading(25)
    ant()
}

fun gun() {
    //val fac = GameOfLife.factory()
    //fac.defaultCellState = GameOfLife.Presets.defaultSquares(2)
    //val auto = fac.create()
    val auto = GameOfLife.automaton()
    val csr = auto.stateRange

    for (c in GameOfLife.Patterns.gosperGliderGun) {
        auto.setCell(c.x, c.y, csr.makeCellState(1))
    }

    for (i in 0..101) {
        if (i > 0) auto.step()
        println("$i:")
        auto.printBoard(-20, 20, -16, 6, GameOfLife.Presets.displayBgBlackWhite(3))
    }
}

fun gunRGBY() {
    val auto = GameOfLifeRGBY.automaton()
    val csr = auto.stateRange
    val rng = Random(42)

    for (c in GameOfLife.Patterns.gosperGliderGun) {
        auto.setCell(c.x, c.y, csr.makeCellState(rng.nextInt(1, 5)))
    }

    for (i in 0..101) {
        if (i > 0) auto.step()
        println("$i:")
        auto.printBoard(-20, 20, -16, 6, GameOfLifeRGBY.displayRGBY)
    }
}

fun gunFading(lifespan: Int, dyingOfOldAge: Boolean = true) {
    val auto = GameOfLifeFading.automaton(lifespan, dyingOfOldAge)
    val csr = auto.stateRange
    val rng = Random(42)

    for (c in GameOfLife.Patterns.gosperGliderGun) {
        auto.setCell(c.x, c.y, csr.makeCellState(rng.nextInt(max(1, lifespan - 4), lifespan + 1)))
    }

    for (i in 0..101) {
        if (i > 0) auto.step()
        println("$i:")
        auto.printBoard(-20, 20, -16, 6, GameOfLifeFading.displayFading(lifespan))
    }
}

fun ant() {
    val auto = LangtonsAnt.automaton()
    auto.setCell(0, 0, auto.stateRange.makeCellState(0, 1))

    for (i in 0..26) {
        if (i > 0) auto.step()
        println("$i:")
        auto.printBoard(-20, 20, -20, 20, LangtonsAnt.displayAnt)
    }

    auto.step(11_000)
    println("11026:")
    auto.printBoard(-40, 30, -40, 40, LangtonsAnt.displayAnt)
}