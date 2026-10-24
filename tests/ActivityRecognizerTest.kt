// SkiTracker
// © 2026 RHerAle
// SPDX-License-Identifier: AGPL-3.0-only
package com.skitracker.service

import com.skitracker.data.ActivityType
import com.skitracker.data.LocationPoint
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for ActivityRecognizer
 */
class ActivityRecognizerTest {
    
    private lateinit var recognizer: ActivityRecognizer
    
    @Before
    fun setup() {
        recognizer = ActivityRecognizer()
    }
    
    @Test
    fun testStationaryDetection() {
        val points = listOf(
            LocationPoint(
                timestamp = 1000,
                latitude = 45.0,
                longitude = 7.0,
                altitude = 1500.0,
                speed = 0.5f,
                accuracy = 10f
            ),
            LocationPoint(
                timestamp = 2000,
                latitude = 45.0001,
                longitude = 7.0001,
                altitude = 1500.0,
                speed = 0.6f,
                accuracy = 10f
            )
        )
        
        val activity = recognizer.analyzeActivity(points)
        // May take a few readings to settle
        assertTrue(activity == ActivityType.STATIONARY)
    }
    
    @Test
    fun testSkiingDetection() {
        val points = mutableListOf<LocationPoint>()
        var altitude = 2000.0
        
        // Simulate skiing down - high speed, losing altitude
        for (i in 0..10) {
            points.add(
                LocationPoint(
                    timestamp = i * 1000L,
                    latitude = 45.0 + i * 0.001,
                    longitude = 7.0 + i * 0.001,
                    altitude = altitude,
                    speed = 12f, // 43 km/h - moderate skiing speed
                    accuracy = 10f
                )
            )
            altitude -= 10.0 // Losing 10m altitude per second
        }
        
        var activity = ActivityType.STATIONARY
        // Run through the points to let hysteresis settle
        for (i in 2..10) {
            activity = recognizer.analyzeActivity(points.subList(i - 2, i + 1))
        }
        
        assertEquals(ActivityType.SKIING, activity)
    }
    
    @Test
    fun testLiftDetection() {
        val points = mutableListOf<LocationPoint>()
        var altitude = 1500.0
        
        // Simulate lift ride - moderate speed, gaining altitude
        for (i in 0..10) {
            points.add(
                LocationPoint(
                    timestamp = i * 1000L,
                    latitude = 45.0 + i * 0.0005,
                    longitude = 7.0 + i * 0.0005,
                    altitude = altitude,
                    speed = 5f, // 18 km/h - typical lift speed
                    accuracy = 10f
                )
            )
            altitude += 8.0 // Gaining 8m altitude per second
        }
        
        var activity = ActivityType.STATIONARY
        // Run through the points to let hysteresis settle
        for (i in 2..10) {
            activity = recognizer.analyzeActivity(points.subList(i - 2, i + 1))
        }
        
        assertEquals(ActivityType.LIFT, activity)
    }

    @Test
    fun testHysteresisTransitions_Stationary_Skiing_Lift_Stationary() {
        val skiingPoints = listOf(
            LocationPoint(timestamp = 0, latitude = 45.0, longitude = 7.0, altitude = 2000.0, speed = 12f, accuracy = 5f),
            LocationPoint(timestamp = 1000, latitude = 45.001, longitude = 7.001, altitude = 1990.0, speed = 12f, accuracy = 5f)
        )
        val liftPoints = listOf(
            LocationPoint(timestamp = 0, latitude = 45.0, longitude = 7.0, altitude = 1500.0, speed = 5f, accuracy = 5f),
            LocationPoint(timestamp = 1000, latitude = 45.0005, longitude = 7.0005, altitude = 1508.0, speed = 5f, accuracy = 5f)
        )
        val stationaryPoints = listOf(
            LocationPoint(timestamp = 0, latitude = 45.0, longitude = 7.0, altitude = 1500.0, speed = 0.5f, accuracy = 5f),
            LocationPoint(timestamp = 1000, latitude = 45.0, longitude = 7.0, altitude = 1500.0, speed = 0.5f, accuracy = 5f)
        )

        // Initial state is STATIONARY
        // Reading 1 of SKIING: should remain STATIONARY
        assertEquals(ActivityType.STATIONARY, recognizer.analyzeActivity(skiingPoints))
        // Reading 2 of SKIING: should remain STATIONARY
        assertEquals(ActivityType.STATIONARY, recognizer.analyzeActivity(skiingPoints))
        // Reading 3 of SKIING: hysteresis threshold met -> switches to SKIING
        assertEquals(ActivityType.SKIING, recognizer.analyzeActivity(skiingPoints))

        // Transition from SKIING to LIFT
        // Reading 1 of LIFT: should remain SKIING
        assertEquals(ActivityType.SKIING, recognizer.analyzeActivity(liftPoints))
        // Reading 2 of LIFT: should remain SKIING
        assertEquals(ActivityType.SKIING, recognizer.analyzeActivity(liftPoints))
        // Reading 3 of LIFT: switches to LIFT
        assertEquals(ActivityType.LIFT, recognizer.analyzeActivity(liftPoints))

        // Transition from LIFT to STATIONARY
        // Reading 1 of STATIONARY: should remain LIFT
        assertEquals(ActivityType.LIFT, recognizer.analyzeActivity(stationaryPoints))
        // Reading 2 of STATIONARY: should remain LIFT
        assertEquals(ActivityType.LIFT, recognizer.analyzeActivity(stationaryPoints))
        // Reading 3 of STATIONARY: switches to STATIONARY
        assertEquals(ActivityType.STATIONARY, recognizer.analyzeActivity(stationaryPoints))
    }

    @Test
    fun testFlappingPreventedByHysteresis() {
        val skiingPoints = listOf(
            LocationPoint(timestamp = 0, latitude = 45.0, longitude = 7.0, altitude = 2000.0, speed = 12f, accuracy = 5f),
            LocationPoint(timestamp = 1000, latitude = 45.001, longitude = 7.001, altitude = 1990.0, speed = 12f, accuracy = 5f)
        )
        val liftPoints = listOf(
            LocationPoint(timestamp = 0, latitude = 45.0, longitude = 7.0, altitude = 1500.0, speed = 5f, accuracy = 5f),
            LocationPoint(timestamp = 1000, latitude = 45.0005, longitude = 7.0005, altitude = 1508.0, speed = 5f, accuracy = 5f)
        )

        // Alternating readings should never trigger a switch from STATIONARY
        for (i in 0 until 6) {
            val pts = if (i % 2 == 0) skiingPoints else liftPoints
            val act = recognizer.analyzeActivity(pts)
            assertEquals(ActivityType.STATIONARY, act)
        }
    }

    @Test
    fun testReset() {
        val skiingPoints = listOf(
            LocationPoint(timestamp = 0, latitude = 45.0, longitude = 7.0, altitude = 2000.0, speed = 12f, accuracy = 5f),
            LocationPoint(timestamp = 1000, latitude = 45.001, longitude = 7.001, altitude = 1990.0, speed = 12f, accuracy = 5f)
        )
        // Transition to SKIING
        recognizer.analyzeActivity(skiingPoints)
        recognizer.analyzeActivity(skiingPoints)
        assertEquals(ActivityType.SKIING, recognizer.analyzeActivity(skiingPoints))

        // Reset
        recognizer.reset()

        // After reset, 1 reading of skiing should NOT switch immediately
        assertEquals(ActivityType.STATIONARY, recognizer.analyzeActivity(skiingPoints))
    }
}
