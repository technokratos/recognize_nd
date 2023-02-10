package org.dbk.recognize_gravitation.tools

import org.dbk.recognize_gravitation.tools.AttractionScalar
import org.dbk.recognize_gravitation.tools.Mass
import org.dbk.recognize_gravitation.tools.MassObject
import org.dbk.recognize_gravitation.tools.Point
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class MassObjectTest {
    @Test
    fun getPoint() {
        val masses = listOf(Mass(Point(0.0, 0.0), AttractionScalar(1.0)), Mass(Point(10.0, 0.0), AttractionScalar(1.0)))
        val massObject = MassObject("", masses)
        assertEquals(Point(5.0, 0.0), massObject.point)
    }
    @Test
    fun getPoint1() {
        val masses = listOf(Mass(Point(0.0, 0.0), AttractionScalar(1.0)), Mass(Point(10.0, 10.0), AttractionScalar(1.0)))
        val massObject = MassObject("", masses)
        assertEquals(Point(5.0, 5.0), massObject.point)
    }
    @Test
    fun getPoint2() {
        val masses = listOf(Mass(Point(0.0, 0.0), AttractionScalar(1.0)), Mass(Point(10.0, 10.0), AttractionScalar(10.0)))
        val massObject = MassObject("", masses)
        assertEquals(Point(9.0, 9.0), massObject.point)
    }

}


