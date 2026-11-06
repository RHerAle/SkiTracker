// SkiTracker
// © 2026 RHerAle
// SPDX-License-Identifier: AGPL-3.0-only
package com.skitracker.data

import org.junit.Assert.*
import org.junit.Test

class TelemetryModelsTest {
    private fun pt(t: Long, seg: Int) =
        LocationPoint(t, 45.0, 7.0, 2000.0, 10f, 5f, seg)

    @Test
    fun testTramos_EmptyList() {
        val points = emptyList<LocationPoint>()
        val result = points.tramos()
        assertTrue(result.isEmpty())
    }

    @Test
    fun testTramos_SinglePoint() {
        val points = listOf(pt(1000L, 0))
        val result = points.tramos()
        assertEquals(1, result.size)
        assertEquals(1, result[0].size)
        assertEquals(0, result[0][0].segmento)
    }

    @Test
    fun testTramos_SingleSegment() {
        val points = listOf(pt(1000L, 0), pt(2000L, 0), pt(3000L, 0))
        val result = points.tramos()
        assertEquals(1, result.size)
        assertEquals(3, result[0].size)
        assertEquals(listOf(1000L, 2000L, 3000L), result[0].map { it.timestamp })
    }

    @Test
    fun testTramos_MultipleSegments() {
        val points = listOf(
            pt(1000L, 0),
            pt(2000L, 0),
            pt(3000L, 1),
            pt(4000L, 1),
            pt(5000L, 2)
        )
        val result = points.tramos()
        assertEquals(3, result.size)
        assertEquals(2, result[0].size)
        assertEquals(2, result[1].size)
        assertEquals(1, result[2].size)

        assertEquals(0, result[0][0].segmento)
        assertEquals(1, result[1][0].segmento)
        assertEquals(2, result[2][0].segmento)
    }

    @Test
    fun testTramos_AlternatingSegments() {
        val points = listOf(
            pt(1000L, 0),
            pt(2000L, 1),
            pt(3000L, 0),
            pt(4000L, 1)
        )
        val result = points.tramos()
        assertEquals(4, result.size)
        for (i in 0 until 4) {
            assertEquals(1, result[i].size)
        }
        assertEquals(0, result[0][0].segmento)
        assertEquals(1, result[1][0].segmento)
        assertEquals(0, result[2][0].segmento)
        assertEquals(1, result[3][0].segmento)
    }
}
