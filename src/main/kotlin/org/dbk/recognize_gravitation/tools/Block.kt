package org.dbk.recognize_gravitation.tools

/**
 * @author Kulikov Denis
 * @since 21.07.2021
 */
class Block<P:IPoint<P>, A: AttractionValue<A>>(val id: Long = 0, val graphs: List<Graph<P,A>>, val blockName: String) : Graph<P,A> {
    override fun middle(): P {
        return graphs.map { it.middle() }.reduce { acc: P, point: P -> acc.add(point) }.div(graphs.size.toDouble())
    }

    override fun attractionValue(): A {
        val map = graphs.map { it.attractionValue() }
        return map
            .reduce { acc: A, value: A -> acc.add(value) }
    }


}