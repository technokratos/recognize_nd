package org.dbk.recognize_gravitation.tools

open class Vector<P : IPoint<P>>(
    open val p0: P,
    open val p1: P
)  {


    open fun middle(): P {
        return  p0 + ((p1 - p0) / 2.0)
    }



    open fun length(): Double {
        return (p1 - p0).length()
    }

    fun dir(): P {
        return p1 - p0
    }

}