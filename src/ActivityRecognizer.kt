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

    // ponytail: sliding window activity classifier with hysteresis
    fun analyzeActivity(recentPoints: List<LocationPoint>): ActivityType {
        if (recentPoints.size < 2) return ActivityType.STATIONARY

        var speedSum = 0f
        for (p in recentPoints) speedSum += p.speed
        val avgSpeed = speedSum / recentPoints.size

        val first = recentPoints.first()
        val last = recentPoints.last()
        val dt = (last.timestamp - first.timestamp) / 1000f
        val vz = if (dt > 0f) (last.altitude - first.altitude).toFloat() / dt else 0f

        val candidate = when {
            avgSpeed < 1.5f -> ActivityType.STATIONARY
            avgSpeed in 2.0f..35f && vz < -0.4f -> ActivityType.SKIING
            avgSpeed in 2f..8f && vz > 0.5f -> ActivityType.LIFT
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
