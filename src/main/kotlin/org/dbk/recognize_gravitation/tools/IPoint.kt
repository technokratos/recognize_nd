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
    fun rotate(pointRot: T, sin: Double, cos: Double): T

    operator fun div(denom: Double): T
    operator fun times(scale : Double): T
    fun add(point: T): T

    fun multiply(vector: T): Double

    fun multiply(scale: Double): T
    fun multiply(scalex: Double, scaley: Double): T

    /**
     * [a * b] = [ay * bz - az * by; az * bx -ax * bz; ax * by - ay * bx]
     */
    fun crossProduct(p: T): P3
    fun equalsWithPrecision(p: T): Boolean
    fun match(p: T): Boolean


    fun perpendicularToLine(attractionVector: Vector<T>) : T
    operator fun minus(p: T): T
    operator fun plus(p: T): T


    operator fun times(perpendicularToLine: T): Double
    fun zero(): T
}