package org.dbk.recognize_gravitation.tools

interface  AttractionValue<A : AttractionValue<A>>{
    fun add(other: A) : A
    fun <P:IPoint<P>> weightedPosition(point: P): P
    fun correlation(a: A): A
    fun absoluteValue() : Double
    operator fun div(d: Double): A
    operator fun times(d: Double): A
    fun <P : IPoint<P>> getForceFromPointToPoint(dir: P): P
    operator fun plus(it: A): A {
        return add(it)
    }
}

class AttractionVector<T : IPoint<T>>(private val value: T) : AttractionValue<AttractionVector<T>> {


    override fun add(other: AttractionVector<T>): AttractionVector<T> {

        return AttractionVector(value.add(other.value))
    }

    override fun <P : IPoint<P>> weightedPosition(point:  P): P {
        TODO("Not yet implemented")
    }

    override fun correlation(a: AttractionVector<T>): AttractionVector<T> {
        TODO("Not yet implemented")
    }

    override fun absoluteValue(): Double {
        return value.length()
    }

    override fun div(d: Double): AttractionVector<T> {
        TODO("Not yet implemented")
    }

    override fun times(d: Double): AttractionVector<T> {
        TODO("Not yet implemented")
    }

    override fun <P : IPoint<P>> getForceFromPointToPoint(dir: P): P {
        TODO("Not yet implemented")
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AttractionVector<*>

        if (value != other.value) return false

        return true
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }

}
class AttractionScalar(val value: Double) : AttractionValue<AttractionScalar> {
    override fun add(other: AttractionScalar): AttractionScalar {
        return AttractionScalar(other.value + value)
    }

    override fun <P : IPoint<P>> weightedPosition(point: P): P {
        return point * value
    }

    override fun <P : IPoint<P>> getForceFromPointToPoint(dir: P): P {
        return dir * value
    }

    override fun correlation(a: AttractionScalar): AttractionScalar {
        return AttractionScalar(value * a.value)
    }

    override fun absoluteValue(): Double {
        return value
    }

    override fun div(d: Double): AttractionScalar {
        return AttractionScalar(value/d)
    }

    override fun times(d: Double): AttractionScalar {
        return AttractionScalar(value * d)
    }

    override fun toString(): String {
        return "A($value)"
    }


}

