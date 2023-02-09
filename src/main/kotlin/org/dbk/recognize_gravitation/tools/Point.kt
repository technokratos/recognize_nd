package org.dbk.recognize_gravitation.tools

import java.util.*
import java.util.function.ToDoubleFunction
import kotlin.Comparator
import kotlin.math.abs

/**
 * @author Kulikov Denis
 * @since 19.07.2021
 */
class Point(val x: Double, val y: Double) : IPoint<Point> {
    override fun angleBetween(vector: Point): Double {
        val y = x * vector.y - y * vector.x
        val x = x * vector.x + this.y * vector.y
        return Math.atan2(y, x)
    }

    override fun angleBetween(a: Point, b: Point): Double {
        val ax = a.x - x
        val ay = a.y - y
        val bx = b.x - x
        val by = b.y - y
        val y = ax * by - ay * bx
        val x = ax * bx + ay * by
        return Math.atan2(y, x)
    }

    override fun angle(): Double {
        return Math.atan2(y, x)
    }

    override fun rotateMinus90(): Point {
        return of(y, -x)
    }

    override fun subtract(point: Point): Point {
        return Point(x - point.x, y - point.y)
    }

    override fun length(): Double {
        return Math.sqrt(lengthSquared())
    }

    override fun lengthSquared(): Double {
        return x * x + y * y
    }

    override fun rotate(angle: Double): Point {
        return rotate(Math.sin(angle), Math.cos(angle))
    }

    override fun rotate(pointRot: Point, angle: Double): Point {
        val cos = Math.cos(Math.PI / 180 * angle)
        val sin = Math.sin(Math.PI / 180 * angle)
        val x = pointRot.x + (x - pointRot.x) * cos - (y - pointRot.y) * sin
        val y = pointRot.y + (this.x - pointRot.x) * sin + (y - pointRot.y) * cos
        return Point(x, y)
    }

    override fun rotate(sin: Double, cos: Double): Point {
        return of(x * cos - y * sin, x * sin + y * cos)
    }

    override operator fun div(denom: Double): Point {
        return of(x / denom, y / denom)
    }

    override fun add(point: Point): Point {
        return of(x + point.x, y + point.y)
    }

    /**
     * Scalar multiplication
     */
    override fun multiply(vector: Point): Double {
        return x * vector.x + y * vector.y
    }

    override fun multiply(scale: Double): Point {
        return of(x * scale, y * scale)
    }

    override fun multiply(scalex: Double, scaley: Double): Point {
        return of(x * scalex, y * scaley)
    }

    override fun equalsWithPrecision(p: Point): Boolean {
        return abs(p.x - x) / PRECISION < PRECISION && abs(p.y - y) / PRECISION < PRECISION
    }

    override fun match(p: Point): Boolean {
        return abs(x - p.x) / PRECISION < PRECISION && abs(y - p.y) / PRECISION < PRECISION
    }

    override fun compareTo(other: Point): Int {
        val compare = this.y.compareTo(other.y)
        return if (compare != 0) compare else this.x.compareTo(other.x)
    }

    override fun toString(): String {
        return "$x $y"
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o !is Point) return false
        return equalsWithPrecision(o)
    }

    override fun hashCode(): Int {
        return Objects.hash((x / PRECISION).toLong(), (y / PRECISION).toLong())
    }

    companion object {
        const val PRECISION = 10e-10
        fun of(x: Double, y: Double): Point {
            return Point(x, y)
        }

        fun comparing(keyExtractor: ToDoubleFunction<Point>): Comparator<Point> {

            return Comparator { c1: Point?, c2: Point? ->
                val x: Double = keyExtractor.applyAsDouble(c1)
                val y: Double = keyExtractor.applyAsDouble(c2)
                x.toInt().toDouble().compareTo(y.toInt().toDouble())
            }
        }

        fun compare(firstPoint: Point, secondPoint: Point): Int {
            val compare = java.lang.Double.compare(firstPoint.y, secondPoint.y)
            return if (compare != 0) compare else java.lang.Double.compare(firstPoint.x, secondPoint.x)
        }

        fun comparator(keyExtractor: ToDoubleFunction<Point?>): Comparator<Point> {
            return Comparator { o1: Point?, o2: Point? ->
                val v1: Double = keyExtractor.applyAsDouble(o1)
                val v2: Double = keyExtractor.applyAsDouble(o2)
                Integer.compare(Math.signum(v1 - v2).toInt(), 0)
            }
        }
    }

    /**
     *                                           2                2
    -Cx⋅P0x + Cx⋅P1x - Cy⋅P0y + Cy⋅P1y + P0x  - P0x⋅P1x + P0y  - P0y⋅P1y
    ────────────────────────────────────────────────────────────────────
    2                  2                  2      2
    P0x  - 2⋅P0x⋅P1x + P0y  - 2⋅P0y⋅P1y + P1x  + P1y


     */
    override fun <A : AttractionValue<A>> perpendicularToLine(line: Line<Point, A>) : Point{
        val P0x = line.p0.x
        val P0y = line.p0.y
        val P1x = line.p1.x
        val P1y = line.p1.y
        val Cx = this.x
        val Cy = this.y
        val t = (-Cx*P0x + Cx*P1x - Cy*P0y + Cy*P1y + P0x*P0x  - P0x*P1x + P0y*P0y  - P0y*P1y)/(P0x*P0x  - 2*P0x*P1x + P0y*P0y  - 2*P0y*P1y + P1x*P1x  + P1y*P1y)

        val x = P0x+t*(P1x-P0x)
        val y=  P0y+t*(P1y-P0y)
        return Point(x,y)
    }
}