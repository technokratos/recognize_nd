package org.dbk.recognize_gravitation.tools

interface Graph<P: IPoint<P>, A: AttractionValue<A>> {
    fun fragment(fragmentation: Int): List<Graph<P,A>> {
        return listOf(this)
    }

    fun middle(): P
    fun attractionValue(): A
}