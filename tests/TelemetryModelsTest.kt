// SkiTracker
// © 2026 RHerAle
// SPDX-License-Identifier: AGPL-3.0-only
package com.skitracker.data

import org.junit.Assert.*
import org.junit.Test

class TelemetryModelsTest {

    private fun pt(seg: Int) = LocationPoint(
        timestamp = 1000L,
        latitude = 45.0,
        longitude = 7.0,
        altitude = 2000.0,
        speed = 5f,
        accuracy = 5f,
        segmento = seg
    )

    @Test
    fun testTramos_EmptyList() {
        val empty = emptyList<LocationPoint>()
        assertTrue(empty.tramos().isEmpty())
    }

    @Test
    fun testTramos_SinglePoint() {
        val points = listOf(pt(1))
        val result = points.tramos()
        assertEquals(1, result.size)
        assertEquals(1, result[0].size)
        assertEquals(1, result[0][0].segmento)
    }

    @Test
    fun testTramos_UniformSegment() {
        val points = listOf(pt(2), pt(2), pt(2))
        val result = points.tramos()
        assertEquals(1, result.size)
        assertEquals(3, result[0].size)
    }

    @Test
    fun testTramos_MultipleSegments() {
        val points = listOf(pt(0), pt(0), pt(1), pt(1), pt(1), pt(2))
        val result = points.tramos()
        assertEquals(3, result.size)
        assertEquals(2, result[0].size)
        assertEquals(3, result[1].size)
        assertEquals(1, result[2].size)
    }

    @Test
    fun testTramos_NonConsecutiveRepeatedSegments() {
        // Between two chunks with same segment ID there was a pause (segment 1 -> segment 2 -> segment 1)
        val points = listOf(pt(1), pt(1), pt(2), pt(1))
        val result = points.tramos()
        assertEquals(3, result.size)
        assertEquals(2, result[0].size)
        assertEquals(1, result[1].size)
        assertEquals(1, result[2].size)
    }
}
