// SkiTracker
// © 2026 RHerAle
// SPDX-License-Identifier: AGPL-3.0-only
package com.skitracker.data

import org.junit.Assert.*
import org.junit.Test
import java.util.LinkedList

class TelemetryModelsTest {

    private fun pt(t: Long, seg: Int = 0) =
        LocationPoint(t, 45.0, 7.0, 2000.0, 10f, 5f, seg)

    @Test
    fun testTramos_EmptyList() {
        val points = emptyList<LocationPoint>()
        assertTrue(points.tramos().isEmpty())
    }

    @Test
    fun testTramos_SinglePoint() {
        val points = listOf(pt(1000, 0))
        val tramos = points.tramos()
        assertEquals(1, tramos.size)
        assertEquals(1, tramos[0].size)
        assertEquals(1000L, tramos[0][0].timestamp)
    }

    @Test
    fun testTramos_UniformSegment() {
        val points = listOf(pt(1000, 1), pt(2000, 1), pt(3000, 1))
        val tramos = points.tramos()
        assertEquals(1, tramos.size)
        assertEquals(3, tramos[0].size)
        assertEquals(points, tramos[0])
    }

    @Test
    fun testTramos_MultipleSegments() {
        val points = listOf(
            pt(1000, 0),
            pt(2000, 0),
            pt(3000, 1),
            pt(4000, 1),
            pt(5000, 2)
        )
        val tramos = points.tramos()
        assertEquals(3, tramos.size)
        assertEquals(listOf(points[0], points[1]), tramos[0])
        assertEquals(listOf(points[2], points[3]), tramos[1])
        assertEquals(listOf(points[4]), tramos[2])
    }

    @Test
    fun testTramos_AlternatingSegments() {
        val points = listOf(
            pt(1000, 0),
            pt(2000, 1),
            pt(3000, 0),
            pt(4000, 1)
        )
        val tramos = points.tramos()
        assertEquals(4, tramos.size)
        assertEquals(listOf(points[0]), tramos[0])
        assertEquals(listOf(points[1]), tramos[1])
        assertEquals(listOf(points[2]), tramos[2])
        assertEquals(listOf(points[3]), tramos[3])
    }

    @Test
    fun testTramos_NonRandomAccessList() {
        val points = LinkedList<LocationPoint>()
        points.add(pt(1000, 0))
        points.add(pt(2000, 0))
        points.add(pt(3000, 1))
        val tramos = points.tramos()
        assertEquals(2, tramos.size)
        assertEquals(2, tramos[0].size)
        assertEquals(1, tramos[1].size)
    }
}
