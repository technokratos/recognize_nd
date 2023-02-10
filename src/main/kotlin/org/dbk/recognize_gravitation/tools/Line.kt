package org.dbk.recognize_gravitation.tools

class Line<P : IPoint<P>, A : AttractionValue<A>>(
    internal val p0: P,
    internal val p1: P,
    private val attractionFunction: (Line<P, A>) -> A
) :
    Graph<P, A> {

    private var attractionValue: A = attractionFunction.invoke(this)

    override fun middle(): P {
        return p0.add(p1)
    }

    override fun attractionValue(): A {
        return attractionValue
    }

    fun length(): Double {
        return (p1 - p0).length()
    }

 }

