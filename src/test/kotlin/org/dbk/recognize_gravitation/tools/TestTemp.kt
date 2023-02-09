package org.dbk.recognize_gravitation.tools

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

/**
 * @author Kulikov Denis
 * @since 07.02.2023
 */
class TestTemp {
    @Test
    fun test() {
        val value = AttractionVector(Point(10.0, 5.0))
        val added = value.add(AttractionVector(Point(1.0, 1.0)))
        assertEquals(added, AttractionVector(Point(11.0, 6.0)))
    }
}