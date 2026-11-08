// SkiTracker
// © 2026 RHerAle
// SPDX-License-Identifier: AGPL-3.0-only
package com.skitracker.data

import org.junit.Assert.*
import org.junit.Test
import java.util.LinkedList

class TelemetryModelsTest {
    private fun pt(t: Long, segmento: Int) =
        LocationPoint(t, 45.0, 7.0, 2000.0, 10f, 5f, segmento)

    @Test
    fun testTramos_EmptyList() {
        val points = emptyList<LocationPoint>()
        assertTrue(points.tramos().isEmpty())
    }

    @Test
    fun testTramos_SinglePoint() {
        val points = listOf(pt(1000L, 0))
        val tramos = points.tramos()
        assertEquals(1, tramos.size)
        assertEquals(1, tramos[0].size)
        assertEquals(1000L, tramos[0][0].timestamp)
    }

    @Test
    fun testTramos_SingleSegmentMultiplePoints() {
        val points = listOf(pt(1000L, 0), pt(2000L, 0), pt(3000L, 0))
        val tramos = points.tramos()
        assertEquals(1, tramos.size)
        assertEquals(3, tramos[0].size)
    }

    @Test
    fun testTramos_MultipleConsecutiveSegments() {
        val points = listOf(
            pt(1000L, 0),
            pt(2000L, 0),
            pt(3000L, 1),
            pt(4000L, 1),
            pt(5000L, 2)
        )
        val tramos = points.tramos()
        assertEquals(3, tramos.size)
        assertEquals(listOf(1000L, 2000L), tramos[0].map { it.timestamp })
        assertEquals(listOf(3000L, 4000L), tramos[1].map { it.timestamp })
        assertEquals(listOf(5000L), tramos[2].map { it.timestamp })
    }

    @Test
    fun testTramos_NonConsecutiveAlternatingSegments() {
        // Pauses that switch segments back and forth
        val points = listOf(
            pt(1000L, 0),
            pt(2000L, 1),
            pt(3000L, 0),
            pt(4000L, 0)
        )
        val tramos = points.tramos()
        assertEquals(3, tramos.size)
        assertEquals(1, tramos[0].size)
        assertEquals(0, tramos[0][0].segmento)
        assertEquals(1, tramos[1].size)
        assertEquals(1, tramos[1][0].segmento)
        assertEquals(2, tramos[2].size)
        assertEquals(0, tramos[2][0].segmento)
    }

    @Test
    fun testTramos_NonRandomAccessList() {
        val points = LinkedList<LocationPoint>()
        points.add(pt(1000L, 0))
        points.add(pt(2000L, 0))
        points.add(pt(3000L, 1))

        val tramos = points.tramos()
        assertEquals(2, tramos.size)
        assertEquals(2, tramos[0].size)
        assertEquals(1, tramos[1].size)
    }
}
