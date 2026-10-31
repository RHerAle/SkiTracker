// SkiTracker
// © 2026 RHerAle
// SPDX-License-Identifier: AGPL-3.0-only
package com.skitracker.data

import org.junit.Assert.*
import org.junit.Test

class TelemetryModelsTest {

    private fun pt(t: Long, segmento: Int = 0) =
        LocationPoint(t, 45.0, 7.0, 2000.0, 10f, 5f, segmento)

    @Test
    fun testTramos_EmptyList() {
        val points = emptyList<LocationPoint>()
        val result = points.tramos()
        assertTrue(result.isEmpty())
    }

    @Test
    fun testTramos_SinglePoint() {
        val points = listOf(pt(1000, 0))
        val result = points.tramos()
        assertEquals(1, result.size)
        assertEquals(1, result[0].size)
        assertEquals(points[0], result[0][0])
    }

    @Test
    fun testTramos_SingleSegmentMultiplePoints() {
        val points = listOf(pt(1000, 0), pt(2000, 0), pt(3000, 0))
        val result = points.tramos()
        assertEquals(1, result.size)
        assertEquals(3, result[0].size)
        assertEquals(points, result[0])
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
        val result = points.tramos()
        assertEquals(3, result.size)
        assertEquals(listOf(points[0], points[1]), result[0])
        assertEquals(listOf(points[2], points[3]), result[1])
        assertEquals(listOf(points[4]), result[2])
    }

    @Test
    fun testTramos_InterleavedSegments() {
        // If segments change, a new tramo is created each time
        val points = listOf(
            pt(1000, 0),
            pt(2000, 1),
            pt(3000, 0)
        )
        val result = points.tramos()
        assertEquals(3, result.size)
        assertEquals(listOf(points[0]), result[0])
        assertEquals(listOf(points[1]), result[1])
        assertEquals(listOf(points[2]), result[2])
    }
}
