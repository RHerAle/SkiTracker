// SkiTracker
// © 2026 RHerAle
// SPDX-License-Identifier: AGPL-3.0-only
package com.skitracker.service

import com.skitracker.data.ActivityType

class CalorieCalculator {
    // ponytail: MET formula (MET * weight * 3.5) / 200
    fun calculateCaloriesPerMinute(weightKg: Float, activity: ActivityType, speedMs: Float = 0f): Float {
        val met = when (activity) {
            ActivityType.SKIING -> when {
                speedMs >= 15f -> 8.0f
                speedMs >= 8f -> 5.3f
                else -> 4.3f
            }
            ActivityType.LIFT -> 1.5f
            ActivityType.STATIONARY -> 1.3f
        }
        return (met * weightKg * 3.5f) / 200f
    }

    fun calculateTotalCalories(weightKg: Float, activity: ActivityType, durationMinutes: Float, avgSpeedMs: Float = 0f): Float =
        calculateCaloriesPerMinute(weightKg, activity, avgSpeedMs) * durationMinutes
}
