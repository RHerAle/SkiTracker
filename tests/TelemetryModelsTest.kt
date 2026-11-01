// SkiTracker
// © 2026 RHerAle
// SPDX-License-Identifier: AGPL-3.0-only
package com.skitracker.data

import org.junit.Assert.*
import org.junit.Test

class TelemetryModelsTest {
    private fun pt(t: Long, seg: Int = 0) =
        LocationPoint(t, 45.0, 7.0, 2000.0, 10f, 5f, segmento = seg)

    @Test
    fun testTramos_EmptyList() {
        val empty = emptyList<LocationPoint>()
        val result = empty.tramos()
        assertTrue(result.isEmpty())
    }

    @Test
    fun testTramos_SinglePoint() {
        val points = listOf(pt(1000, 0))
        val result = points.tramos()
        assertEquals(1, result.size)
        assertEquals(1, result[0].size)
        assertEquals(1000L, result[0][0].timestamp)
    }

    @Test
    fun testTramos_ContinuousSingleSegment() {
        val points = listOf(pt(1000, 0), pt(2000, 0), pt(3000, 0))
        val result = points.tramos()
        assertEquals(1, result.size)
        assertEquals(3, result[0].size)
    }

    @Test
    fun testTramos_MultipleSegments() {
        val points = listOf(
            pt(1000, 0),
            pt(2000, 0),
            pt(3000, 1),
            pt(4000, 1),
            pt(5000, 2),
            pt(6000, 0) // resuming segment 0 creates a new segment chunk since pause occurred
        )
        val result = points.tramos()
        assertEquals(4, result.size)
        assertEquals(2, result[0].size)
        assertEquals(2, result[1].size)
        assertEquals(1, result[2].size)
        assertEquals(1, result[3].size)

        assertEquals(0, result[0][0].segmento)
        assertEquals(1, result[1][0].segmento)
        assertEquals(2, result[2][0].segmento)
        assertEquals(0, result[3][0].segmento)
    }
}
