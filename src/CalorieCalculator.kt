// SkiTracker
// © 2026 RHerAle
// SPDX-License-Identifier: AGPL-3.0-only
package com.skitracker.service

import com.skitracker.data.ActivityType

class CalorieCalculator {
    // ponytail: MET formula (MET * weight * 3.5) / 200 with constant 0.0175f
    fun calculateCaloriesPerMinute(weightKg: Float, activity: ActivityType, speedMs: Float = 0f): Float {
        if (weightKg <= 0f || !weightKg.isFinite() || speedMs < 0f || !speedMs.isFinite()) return 0f
        val met = when (activity) {
            ActivityType.SKIING -> when {
                speedMs >= 15f -> 8.0f
                speedMs >= 8f -> 5.3f
                else -> 4.3f
            }
            ActivityType.LIFT -> 1.5f
            ActivityType.STATIONARY -> 1.3f
        }
        return met * weightKg * 0.0175f
    }

    fun calculateTotalCalories(weightKg: Float, activity: ActivityType, durationMinutes: Float, avgSpeedMs: Float = 0f): Float {
        if (durationMinutes <= 0f || !durationMinutes.isFinite()) return 0f
        return calculateCaloriesPerMinute(weightKg, activity, avgSpeedMs) * durationMinutes
    }
}
