package org.dbk.recognize_gravitation.tools

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class AttractionVectorTest



    @Test
    fun test() {
        val value = AttractionVector(Point(10.0, 5.0))
        val added = value.add(AttractionVector(Point(1.0, 1.0)))
        assertEquals(added, AttractionVector(Point(11.0, 6.0)))
    }
