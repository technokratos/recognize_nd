package org.dbk.recognize_gravitation.tools

class AttractionLine<P : IPoint<P>, A : AttractionValue<A>>(
    override val p0: P,
    override val p1: P,
    private val attractionFunction: (AttractionLine<P, A>) -> A
) : Vector<P>(p0, p1), Graph<P, A> {

    private var attractionValue: A = attractionFunction.invoke(this)

//    override fun middle(): P {
//        return  p0 + ((p1 - p0) / 2.0)
//    }

    override fun attractionValue(): A {
        return attractionValue
    }

//    override fun length(): Double {
//        return (p1 - p0).length()
//    }

 }

