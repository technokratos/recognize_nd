package org.dbk.recognize_gravitation.tools

import kotlin.math.cos
import kotlin.math.sin

class Mass<T : IPoint<T>, A : AttractionValue<A>>(val point: T, val attraction: A) {
    fun rotateAndMove(center: T, shiftPosition: T, sin: Double, cos: Double): Mass<T, A> {
        if (center is P3) {
            TODO("Not implement yet rotation, need select axis")
        }

        return Mass(point.rotate(center, sin, cos) + shiftPosition, this.attraction)
    }

    override fun toString(): String {
        return "$point, [$attraction]"
    }


}

class MassObject<T : IPoint<T>, A : AttractionValue<A>>(
    val name: String,
    val masses: List<Mass<T, A>>
) {
    private var momentOfInertia: Double? = null
    val point: T
    val value: A

    init {
        val value1 = masses.first().attraction
        val firstPoint = value1.weightedPosition(masses.first().point)

        value = masses
            .asSequence()
            .map { it.attraction }
            .reduce { acc: A, it: A -> acc + it }
        val weightedPoint = masses
            .asSequence()
            .map { it.attraction.weightedPosition(it.point) }
            .reduce { acc, p -> acc + p }

        point = weightedPoint / value.absoluteValue()

        if (point is Point) {
            momentOfInertia = masses.sumOf {
                val radiusSquared = (it.point - point).lengthSquared()
                it.attraction.absoluteValue() * radiusSquared
            }
        }
    }


    fun momentOfInertiaAroundOfAxis(resultantMoment: P3): Double {
        return momentOfInertia ?: TODO("Not yet implemented for P3")
    }

    fun rotateAndMove(
        angle: Double,
        center: T,
        shiftPosition: T
    ): MassObject<T, A> {

        val sin = sin(Math.PI / 180 * angle)
        val cos = cos(Math.PI / 180 * angle)
        return MassObject(this.name, this.masses.map {
            it.rotateAndMove(
                center,
                shiftPosition,
                sin,
                cos
            )
        })

    }

    override fun toString(): String {
        return "$point $value $momentOfInertia, $name"
    }


}