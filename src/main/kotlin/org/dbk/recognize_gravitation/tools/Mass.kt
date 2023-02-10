package org.dbk.recognize_gravitation.tools

class Mass<T: IPoint<T>, A: AttractionValue<A>>(val point: T, val attraction: A) {

}

class MassObject<T: IPoint<T>, A: AttractionValue<A>>(val name: String,

                                                      val masses: List<Mass<T, A>>) {
    val point: T
    val value: A

    init {
        val value1 = masses.first().attraction
        val firstPoint = value1.weightedPosition(masses.first().point)

        //val totalMass = masses.map { it.value }.reduce { acc: A, it: A -> acc.add(it) }
        value = masses.map { it.attraction }.reduce { acc: A, it: A -> acc.add(it) }
        val weightedPoint = masses.map { it.attraction.weightedPosition(it.point) }.reduce { acc, p -> acc.add(p) }

//        val weightedPoint = masses.stream()
//            .skip(1)
//            .map { it.attraction.weightedPosition(it.point) }
//            .collect(
//                { firstPoint },
//                { left, right -> left.add(right) },
//                { left, right -> left.add(right) })
        point = weightedPoint / value.absoluteValue()

    }


}