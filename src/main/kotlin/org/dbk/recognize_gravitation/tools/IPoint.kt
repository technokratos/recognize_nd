package org.dbk.recognize_gravitation.tools

interface IPoint<T : IPoint<T>>: Comparable<T> {
    fun angleBetween(vector: T): Double
    fun angleBetween(a: T, b: T): Double
    fun angle(): Double
    fun rotateMinus90(): T
    fun subtract(point: T): T
    fun length(): Double
    fun lengthSquared(): Double
    fun rotate(angle: Double): T
    fun rotate(pointRot: T, angle: Double): T
    fun rotate(sin: Double, cos: Double): T

    operator fun div(denom: Double): T
    operator fun times(scale : Double): T
    fun add(point: T): T

    fun multiply(vector: T): Double

    fun multiply(scale: Double): T
    fun multiply(scalex: Double, scaley: Double): T
    fun equalsWithPrecision(p: T): Boolean
    fun match(p: T): Boolean


    fun <A: AttractionValue<A>> perpendicularToLine(line: Line<T,A>) : T
    operator fun minus(p0: T): T
}