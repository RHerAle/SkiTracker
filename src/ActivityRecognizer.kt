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
        for (i in 0 until size) {
            val speed = recentPoints[i].speed
            // Sentinel: validate speed values defensively against negative, NaN, or non-finite readings
            if (speed < 0f || !speed.isFinite()) return ActivityType.STATIONARY
            speedSum += speed
        }
        val avgSpeed = speedSum / size

        val first = recentPoints[0]
        val last = recentPoints[size - 1]
        val dt = (last.timestamp - first.timestamp) / 1000f
        // Sentinel: require positive duration and finite altitudes to prevent division anomalies or NaN propagation
        if (dt <= 0f || !first.altitude.isFinite() || !last.altitude.isFinite()) return ActivityType.STATIONARY
        val vz = (last.altitude - first.altitude).toFloat() / dt
        if (!vz.isFinite()) return ActivityType.STATIONARY

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
