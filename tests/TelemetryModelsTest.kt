// SkiTracker
// © 2026 RHerAle
// SPDX-License-Identifier: AGPL-3.0-only
package com.skitracker.data

import org.junit.Assert.*
import org.junit.Test

class TelemetryModelsTest {
    private fun pt(seg: Int) = LocationPoint(1000L, 45.0, 7.0, 2000.0, 10f, 5f, seg)

    @Test
    fun testTramosEmptyList() {
        val empty = emptyList<LocationPoint>()
        val result = empty.tramos()
        assertTrue(result.isEmpty())
    }

    @Test
    fun testTramosSinglePoint() {
        val points = listOf(pt(0))
        val result = points.tramos()
        assertEquals(1, result.size)
        assertEquals(1, result[0].size)
        assertEquals(0, result[0][0].segmento)
    }

    @Test
    fun testTramosSingleSegment() {
        val points = listOf(pt(1), pt(1), pt(1))
        val result = points.tramos()
        assertEquals(1, result.size)
        assertEquals(3, result[0].size)
    }

    @Test
    fun testTramosMultipleSegments() {
        val points = listOf(pt(0), pt(0), pt(1), pt(1), pt(2))
        val result = points.tramos()
        assertEquals(3, result.size)
        assertEquals(listOf(0, 0), result[0].map { it.segmento })
        assertEquals(listOf(1, 1), result[1].map { it.segmento })
        assertEquals(listOf(2), result[2].map { it.segmento })
    }

    @Test
    fun testTramosNonConsecutiveRepeatedSegments() {
        // Pauses that switch back to the same segment index should create separate chunks
        val points = listOf(pt(0), pt(1), pt(0))
        val result = points.tramos()
        assertEquals(3, result.size)
        assertEquals(1, result[0].size)
        assertEquals(1, result[1].size)
        assertEquals(1, result[2].size)
        assertEquals(0, result[0][0].segmento)
        assertEquals(1, result[1][0].segmento)
        assertEquals(0, result[2][0].segmento)
    }
}
