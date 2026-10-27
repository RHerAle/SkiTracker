// SkiTracker
// © 2026 RHerAle
// SPDX-License-Identifier: AGPL-3.0-only
package com.skitracker.service

import com.skitracker.data.ActivityType
import com.skitracker.data.LocationPoint

class ActivityRecognizer {
    private var currentActivity = ActivityType.STATIONARY
    private var consistentReadings = 0
    private var lastCandidateActivity = ActivityType.STATIONARY

    // ponytail: zero-alloc sliding window activity classifier with hysteresis
    fun analyzeActivity(recentPoints: List<LocationPoint>): ActivityType {
        val size = recentPoints.size
        if (size < 2) return ActivityType.STATIONARY

        var speedSum = 0f
        var validCount = 0
        var firstValid: LocationPoint? = null
        var lastValid: LocationPoint? = null

        // Security / Defensive check: filter out non-finite or invalid telemetry points
        for (i in 0 until size) {
            val p = recentPoints[i]
            if (p.speed.isFinite() && p.speed >= 0f && p.altitude.isFinite()) {
                if (firstValid == null) firstValid = p
                lastValid = p
                speedSum += p.speed
                validCount++
            }
        }

        if (validCount < 2 || firstValid == null || lastValid == null) return ActivityType.STATIONARY

        val avgSpeed = speedSum / validCount
        val dt = (lastValid.timestamp - firstValid.timestamp) / 1000f
        val vz = if (dt > 0f && dt.isFinite()) (lastValid.altitude - firstValid.altitude).toFloat() / dt else 0f

        val candidate = when {
            avgSpeed < 1.5f -> ActivityType.STATIONARY
            avgSpeed >= 2.0f && avgSpeed <= 35.0f && vz < -0.4f -> ActivityType.SKIING
            avgSpeed >= 2.0f && avgSpeed <= 8.0f && vz > 0.5f -> ActivityType.LIFT
            else -> ActivityType.STATIONARY
        }

        if (candidate == lastCandidateActivity) {
            if (++consistentReadings >= 3) currentActivity = candidate
        } else {
            lastCandidateActivity = candidate
            consistentReadings = 1
        }
        return currentActivity
    }

    fun reset() {
        currentActivity = ActivityType.STATIONARY
        consistentReadings = 0
        lastCandidateActivity = ActivityType.STATIONARY
    }
}
