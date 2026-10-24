// SkiTracker
// © 2026 RHerAle
// SPDX-License-Identifier: AGPL-3.0-only
package com.skitracker.service

import com.skitracker.data.ActivityType
import com.skitracker.data.LocationPoint
import org.junit.Assert.*
import org.junit.Test

class ActivityRecognizerTest {
    private val recognizer = ActivityRecognizer()

    private fun pt(t: Long, alt: Double, speed: Float) =
        LocationPoint(t, 45.0, 7.0, alt, speed, 5f)

    @Test
    fun testStationaryDetection() {
        val points = listOf(pt(1000, 1500.0, 0.5f), pt(2000, 1500.0, 0.6f))
        assertEquals(ActivityType.STATIONARY, recognizer.analyzeActivity(points))
    }

    @Test
    fun testSkiingDetection() {
        val points = (0..10).map { i -> pt(i * 1000L, 2000.0 - i * 10.0, 12f) }
        var activity = ActivityType.STATIONARY
        for (i in 2..10) activity = recognizer.analyzeActivity(points.subList(i - 2, i + 1))
        assertEquals(ActivityType.SKIING, activity)
    }

    @Test
    fun testLiftDetection() {
        val points = (0..10).map { i -> pt(i * 1000L, 1500.0 + i * 8.0, 5f) }
        var activity = ActivityType.STATIONARY
        for (i in 2..10) activity = recognizer.analyzeActivity(points.subList(i - 2, i + 1))
        assertEquals(ActivityType.LIFT, activity)
    }

    @Test
    fun testHysteresisTransitions_Stationary_Skiing_Lift_Stationary() {
        val skiing = listOf(pt(0, 2000.0, 12f), pt(1000, 1990.0, 12f))
        val lift = listOf(pt(0, 1500.0, 5f), pt(1000, 1508.0, 5f))
        val stationary = listOf(pt(0, 1500.0, 0.5f), pt(1000, 1500.0, 0.5f))

        assertEquals(ActivityType.STATIONARY, recognizer.analyzeActivity(skiing))
        assertEquals(ActivityType.STATIONARY, recognizer.analyzeActivity(skiing))
        assertEquals(ActivityType.SKIING, recognizer.analyzeActivity(skiing))

        assertEquals(ActivityType.SKIING, recognizer.analyzeActivity(lift))
        assertEquals(ActivityType.SKIING, recognizer.analyzeActivity(lift))
        assertEquals(ActivityType.LIFT, recognizer.analyzeActivity(lift))

        assertEquals(ActivityType.LIFT, recognizer.analyzeActivity(stationary))
        assertEquals(ActivityType.LIFT, recognizer.analyzeActivity(stationary))
        assertEquals(ActivityType.STATIONARY, recognizer.analyzeActivity(stationary))
    }

    @Test
    fun testFlappingPreventedByHysteresis() {
        val skiing = listOf(pt(0, 2000.0, 12f), pt(1000, 1990.0, 12f))
        val lift = listOf(pt(0, 1500.0, 5f), pt(1000, 1508.0, 5f))
        for (i in 0 until 6) {
            val pts = if (i % 2 == 0) skiing else lift
            assertEquals(ActivityType.STATIONARY, recognizer.analyzeActivity(pts))
        }
    }

    @Test
    fun testReset() {
        val skiing = listOf(pt(0, 2000.0, 12f), pt(1000, 1990.0, 12f))
        recognizer.analyzeActivity(skiing)
        recognizer.analyzeActivity(skiing)
        assertEquals(ActivityType.SKIING, recognizer.analyzeActivity(skiing))

        recognizer.reset()
        assertEquals(ActivityType.STATIONARY, recognizer.analyzeActivity(skiing))
    }
}
