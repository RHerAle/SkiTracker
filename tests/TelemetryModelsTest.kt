// SkiTracker
// © 2026 RHerAle
// SPDX-License-Identifier: AGPL-3.0-only
package com.skitracker.data

import org.junit.Assert.*
import org.junit.Test

class TelemetryModelsTest {
    private fun pt(segmento: Int = 0) =
        LocationPoint(1000L, 45.0, 7.0, 2000.0, 10f, 5f, segmento)

    @Test
    fun testTramos_EmptyList() {
        val empty = emptyList<LocationPoint>()
        assertTrue(empty.tramos().isEmpty())
    }

    @Test
    fun testTramos_SinglePoint() {
        val p = pt(0)
        val tramos = listOf(p).tramos()
        assertEquals(1, tramos.size)
        assertEquals(1, tramos[0].size)
        assertEquals(p, tramos[0][0])
    }

    @Test
    fun testTramos_SingleSegment() {
        val points = listOf(pt(1), pt(1), pt(1))
        val tramos = points.tramos()
        assertEquals(1, tramos.size)
        assertEquals(3, tramos[0].size)
    }

    @Test
    fun testTramos_MultipleSegments() {
        val p1 = pt(0)
        val p2 = pt(0)
        val p3 = pt(1)
        val p4 = pt(1)
        val p5 = pt(2)

        val tramos = listOf(p1, p2, p3, p4, p5).tramos()
        assertEquals(3, tramos.size)
        assertEquals(listOf(p1, p2), tramos[0])
        assertEquals(listOf(p3, p4), tramos[1])
        assertEquals(listOf(p5), tramos[2])
    }

    @Test
    fun testTramos_AlternatingSegments() {
        val p1 = pt(0)
        val p2 = pt(1)
        val p3 = pt(0)

        val tramos = listOf(p1, p2, p3).tramos()
        assertEquals(3, tramos.size)
        assertEquals(listOf(p1), tramos[0])
        assertEquals(listOf(p2), tramos[1])
        assertEquals(listOf(p3), tramos[2])
    }
}
