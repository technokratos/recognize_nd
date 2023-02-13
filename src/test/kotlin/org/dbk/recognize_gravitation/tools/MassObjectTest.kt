package org.dbk.recognize_gravitation.tools

import org.junit.jupiter.api.Assertions.assertEquals
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
        assertEquals(Point(9.090909090909092, 9.090909090909092), massObject.point)
    }

}


