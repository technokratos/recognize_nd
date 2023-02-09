package org.dbk.recognize_gravitation.tools

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class BlockTest {

    @Test
    fun weight() {
        val attractionFunction: (Line<Point, AttractionScalar>) -> AttractionScalar =
            { line -> AttractionScalar(line.p1.subtract(line.p0).length()) }
        val block = Block(0, listOf(Line(Point.of(0.0, 0.0), Point.of(1.0, 0.0), attractionFunction)), "")
        assertEquals(1.0, block.attractionValue().value, 1e-10)
    }
}