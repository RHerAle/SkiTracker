// SkiTracker
// © 2026 RHerAle
// SPDX-License-Identifier: AGPL-3.0-only
package com.skitracker.data

import org.junit.Assert.*
import org.junit.Test

class TelemetryModelsTest {

    private fun pt(t: Long, seg: Int) =
        LocationPoint(t, 45.0, 7.0, 1500.0, 10f, 5f, seg)

    @Test
    fun testTramos_EmptyList() {
        val emptyList = emptyList<LocationPoint>()
        val result = emptyList.tramos()
        assertTrue(result.isEmpty())
    }

    @Test
    fun testTramos_SinglePoint() {
        val points = listOf(pt(1000L, 0))
        val result = points.tramos()
        assertEquals(1, result.size)
        assertEquals(1, result[0].size)
        assertEquals(points[0], result[0][0])
    }

    @Test
    fun testTramos_SingleSegment() {
        val points = listOf(
            pt(1000L, 0),
            pt(2000L, 0),
            pt(3000L, 0)
        )
        val result = points.tramos()
        assertEquals(1, result.size)
        assertEquals(3, result[0].size)
        assertEquals(points, result[0])
    }

    @Test
    fun testTramos_MultipleSegments() {
        val p1 = pt(1000L, 0)
        val p2 = pt(2000L, 0)
        val p3 = pt(3000L, 1)
        val p4 = pt(4000L, 1)
        val p5 = pt(5000L, 2)

        val points = listOf(p1, p2, p3, p4, p5)
        val result = points.tramos()

        assertEquals(3, result.size)
        assertEquals(listOf(p1, p2), result[0])
        assertEquals(listOf(p3, p4), result[1])
        assertEquals(listOf(p5), result[2])
    }

    @Test
    fun testTramos_AlternatingSegments() {
        val p1 = pt(1000L, 0)
        val p2 = pt(2000L, 1)
        val p3 = pt(3000L, 0)

        val points = listOf(p1, p2, p3)
        val result = points.tramos()

        assertEquals(3, result.size)
        assertEquals(listOf(p1), result[0])
        assertEquals(listOf(p2), result[1])
        assertEquals(listOf(p3), result[2])
    }
}
