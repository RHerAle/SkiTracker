// SkiTracker
// © 2026 RHerAle
// SPDX-License-Identifier: AGPL-3.0-only
package com.skitracker.service

import com.skitracker.data.ActivityType
import com.skitracker.data.LocationPoint
import kotlin.math.abs

/**
 * Analyzes GPS data to determine current activity type
 * Uses speed and altitude change to distinguish between skiing and lift rides
 */
class ActivityRecognizer {
    
    // Configuration parameters (can be tuned based on field testing)
    private val SKIING_MIN_SPEED_MS = 5f      // 5 m/s = 18 km/h
    private val SKIING_MAX_SPEED_MS = 30f     // 30 m/s = 108 km/h
    private val LIFT_MIN_SPEED_MS = 2f        // 2 m/s = 7.2 km/h
    private val LIFT_MAX_SPEED_MS = 8f        // 8 m/s = 28.8 km/h
    private val STATIONARY_THRESHOLD_MS = 1.5f // Below this = stationary
    
    // Altitude change threshold (meters per second)
    private val ALTITUDE_CHANGE_THRESHOLD = 0.5f
    
    // Hysteresis: require multiple consistent readings before switching
    private val MIN_CONSISTENT_READINGS = 3
    private var currentActivity = ActivityType.STATIONARY
    private var consistentReadings = 0
    private var lastCandidateActivity = ActivityType.STATIONARY
    
    /**
     * Analyze recent location points to determine activity
     */
    fun analyzeActivity(recentPoints: List<LocationPoint>): ActivityType {
        if (recentPoints.size < 2) {
            return ActivityType.STATIONARY
        }
        
        // Calculate average speed from recent points
        val avgSpeed = recentPoints.map { it.speed }.average().toFloat()
        
        // Calculate altitude change rate
        val altitudeChangeRate = calculateAltitudeChangeRate(recentPoints)
        
        // Determine candidate activity
        val candidateActivity = when {
            avgSpeed < STATIONARY_THRESHOLD_MS -> ActivityType.STATIONARY
            
            // Skiing: higher speed, losing altitude
            avgSpeed >= SKIING_MIN_SPEED_MS && 
            avgSpeed <= SKIING_MAX_SPEED_MS && 
            altitudeChangeRate < -ALTITUDE_CHANGE_THRESHOLD -> ActivityType.SKIING
            
            // Lift: moderate speed, gaining altitude
            avgSpeed >= LIFT_MIN_SPEED_MS && 
            avgSpeed <= LIFT_MAX_SPEED_MS && 
            altitudeChangeRate > ALTITUDE_CHANGE_THRESHOLD -> ActivityType.LIFT
            
            // Default to stationary if uncertain
            else -> ActivityType.STATIONARY
        }
        
        // Apply hysteresis to avoid rapid switching
        if (candidateActivity == lastCandidateActivity) {
            consistentReadings++
            if (consistentReadings >= MIN_CONSISTENT_READINGS) {
                currentActivity = candidateActivity
            }
        } else {
            lastCandidateActivity = candidateActivity
            consistentReadings = 1
        }
        
        return currentActivity
    }
    
    /**
     * Calculate average altitude change rate (meters per second)
     * Positive = gaining altitude, Negative = losing altitude
     */
    private fun calculateAltitudeChangeRate(points: List<LocationPoint>): Float {
        if (points.size < 2) return 0f
        
        val first = points.first()
        val last = points.last()
        
        val altitudeChange = last.altitude - first.altitude
        val timeDiffSeconds = (last.timestamp - first.timestamp) / 1000f
        
        return if (timeDiffSeconds > 0) {
            (altitudeChange / timeDiffSeconds).toFloat()
        } else {
            0f
        }
    }
    
    /**
     * Reset the activity recognizer state
     */
    fun reset() {
        currentActivity = ActivityType.STATIONARY
        consistentReadings = 0
        lastCandidateActivity = ActivityType.STATIONARY
    }
}
