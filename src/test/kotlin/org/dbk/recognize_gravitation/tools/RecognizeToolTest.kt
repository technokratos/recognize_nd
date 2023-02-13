package org.dbk.recognize_gravitation.tools

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class RecognizeToolTest {


    @Test
    fun findNearestPosition() {

        val blocks = emptyList<List<Block<Point, AttractionScalar>>>()
        val attractionFunction: (AttractionLine<Point, AttractionScalar>) -> AttractionScalar =
            { AttractionScalar((it.p1 - it.p0).length()) }

        val attractionLine: AttractionLine<Point, AttractionScalar> =
            AttractionLine(Point(0.0, 0.0), Point(100.0, 0.0), attractionFunction)

        val attractionLine2: AttractionLine<Point, AttractionScalar> =
            AttractionLine(Point(100.0, 0.0), Point(100.0, 50.0), attractionFunction)


        val firstBlock = Block(0, listOf(attractionLine, attractionLine2), "")

        val recognizeTool = RecognizeTool (listOf(firstBlock), shiftK = 10.0, angleK = 10.0 )


        val secondMass = recognizeTool.mapToMass(firstBlock.graphs)

        val firstMass = recognizeTool.mapToMass(listOf(
            AttractionLine(Point(10.0, 10.0), Point(110.0, 10.0), attractionFunction),
            AttractionLine(Point(110.0, 10.0), Point(110.0, 60.0), attractionFunction)
        ))
        val force = recognizeTool.findNearestPosition( firstMass, secondMass)
        assertTrue(force > 0)
    }
}