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
        // Security: Filter out invalid/corrupted GPS sensor readings (NaN, Infinite, negative speed)
        val validPoints = recentPoints.filter { pt ->
            pt.speed >= 0f && pt.speed.isFinite() && pt.altitude.isFinite()
        }
        val size = validPoints.size
        if (size < 2) return ActivityType.STATIONARY

        var speedSum = 0f
        for (i in 0 until size) speedSum += validPoints[i].speed
        val avgSpeed = speedSum / size

        val first = validPoints[0]
        val last = validPoints[size - 1]
        val dt = (last.timestamp - first.timestamp) / 1000f
        val vz = if (dt > 0f) (last.altitude - first.altitude).toFloat() / dt else 0f

        if (!avgSpeed.isFinite() || !vz.isFinite()) return ActivityType.STATIONARY

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
