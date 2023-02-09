package org.dbk.recognize_gravitation.tools

class Mass<T: IPoint<T>, A: AttractionValue<A>>(val point: T, val attraction: A) {

}

class MassObject<T: IPoint<T>, A: AttractionValue<A>>(val name: String, val point: T, val value: A, val masses: List<Mass<T, A>>) {

}