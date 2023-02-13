package org.dbk.recognize_gravitation.tools

class P3(val x: Double, val y: Double, val z: Double): IPoint<P3> {
    override fun angleBetween(vector: P3): Double {
        TODO("Not yet implemented")
    }

    override fun angleBetween(a: P3, b: P3): Double {
        TODO("Not yet implemented")
    }

    override fun angle(): Double {
        TODO("Not yet implemented")
    }

    override fun rotateMinus90(): P3 {
        TODO("Not yet implemented")
    }

    override fun length(): Double {
        TODO("Not yet implemented")
    }

    override fun lengthSquared(): Double {
        TODO("Not yet implemented")
    }

    override fun rotate(angle: Double): P3 {
        TODO("Not yet implemented")
    }

    override fun rotate(sin: Double, cos: Double): P3 {
        TODO("Not yet implemented")
    }

    override fun rotate(pointRot: P3, sin: Double, cos: Double): P3 {
        return P3(0.0, 0.0, 0.0)
    }

    override fun div(denom: Double): P3 {
        TODO("Not yet implemented")
    }

    override fun times(scale: Double): P3 {
        TODO("Not yet implemented")
    }

    override fun times(perpendicularToLine: P3): Double {
        TODO("Not yet implemented")
    }

    override fun multiply(scale: Double): P3 {
        TODO("Not yet implemented")
    }

    override fun multiply(scalex: Double, scaley: Double): P3 {
        TODO("Not yet implemented")
    }

    override fun zero(): P3 {
        TODO("Not yet implemented")
    }

    override fun compareTo(other: P3): Int {
        TODO("Not yet implemented")
    }

    override fun plus(p: P3): P3 {
        return P3(x + p.x, y + p.y, z + p.z)
    }

    override fun minus(p: P3): P3 {
        TODO("Not yet implemented")
    }

    override fun perpendicularToLine(attractionVector: Vector<P3>): P3 {
        TODO("Not yet implemented")
    }

    override fun match(p: P3): Boolean {
        TODO("Not yet implemented")
    }

    override fun equalsWithPrecision(p: P3): Boolean {
        TODO("Not yet implemented")
    }

    /**
     *
     */
    override fun crossProduct(p: P3): P3 {
        val ax = this.x
        val ay = this.y
        val az = this.z
        val bx = p.x
        val by = p.y
        val bz = p.z
        return P3(ay * bz - az * by, az * bx -ax * bz, ax * by - ay * bx)
    }

    override fun multiply(vector: P3): Double {
        TODO("Not yet implemented")
    }

    override fun add(point: P3): P3 {
        TODO("Not yet implemented")
    }

    override fun rotate(pointRot: P3, angle: Double): P3 {
        TODO("Not yet implemented")
    }

    override fun subtract(point: P3): P3 {
        TODO("Not yet implemented")
    }

    override fun toString(): String {
        return "[$x;$y;$z]"
    }
}