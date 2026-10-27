// SkiTracker
// © 2026 RHerAle
// SPDX-License-Identifier: AGPL-3.0-only
package com.skitracker.data

import org.junit.Assert.*
import org.junit.Test

class TelemetryModelsTest {

    private fun pt(ts: Long, segment: Int) =
        LocationPoint(ts, 45.0, 7.0, 2000.0, 10f, 5f, segmento = segment)

    @Test
    fun testTramos_EmptyList() {
        val empty = emptyList<LocationPoint>()
        assertTrue(empty.tramos().isEmpty())
    }

    @Test
    fun testTramos_SingleSegment() {
        val points = listOf(pt(1000, 0), pt(2000, 0), pt(3000, 0))
        val tramos = points.tramos()
        assertEquals(1, tramos.size)
        assertEquals(3, tramos[0].size)
        assertEquals(points, tramos[0])
    }

    @Test
    fun testTramos_MultipleSegments() {
        val p0 = pt(1000, 0)
        val p1 = pt(2000, 0)
        val p2 = pt(3000, 1)
        val p3 = pt(4000, 1)
        val p4 = pt(5000, 2)
        val points = listOf(p0, p1, p2, p3, p4)

        val tramos = points.tramos()
        assertEquals(3, tramos.size)
        assertEquals(listOf(p0, p1), tramos[0])
        assertEquals(listOf(p2, p3), tramos[1])
        assertEquals(listOf(p4), tramos[2])
    }

    @Test
    fun testTramos_SegmentSwitchAndReturn() {
        val p0 = pt(1000, 0)
        val p1 = pt(2000, 1)
        val p2 = pt(3000, 0)
        val points = listOf(p0, p1, p2)

        val tramos = points.tramos()
        assertEquals(3, tramos.size)
        assertEquals(listOf(p0), tramos[0])
        assertEquals(listOf(p1), tramos[1])
        assertEquals(listOf(p2), tramos[2])
    }
}
