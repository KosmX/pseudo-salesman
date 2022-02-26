package dev.kosmx.pseudoSalesman

import kotlin.math.sqrt

/**
 * 2d vector with required features
 */
data class Pos(val x: Double, val y: Double) {

    operator fun plus(a: Pos): Pos {
        return Pos(this.x + a.x, this.y + a.y)
    }

    operator fun unaryMinus(): Pos {
        return Pos(-this.x, -this.y)
    }

    operator fun minus(rho: Pos): Pos {
        return this + (-rho)
    }

    infix fun distance(other: Pos): Double {
        return sqrt((this - other).let { it.x * it.x + it.y * it.y })
    }
}
