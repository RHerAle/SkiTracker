// SkiTracker
// © 2026 RHerAle
// SPDX-License-Identifier: AGPL-3.0-only
package com.skitracker.service

import com.skitracker.data.ActivityType

class CalorieCalculator {
    // ponytail: MET formula (MET * weight * 3.5) / 200
    fun calculateCaloriesPerMinute(weightKg: Float, activity: ActivityType, speedMs: Float = 0f): Float {
        // Security input validation: prevent negative or non-finite values from distorting calculations
        require(weightKg.isFinite() && weightKg > 0f) { "Weight must be positive and finite" }
        require(speedMs.isFinite() && speedMs >= 0f) { "Speed must be non-negative and finite" }

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

    fun calculateTotalCalories(weightKg: Float, activity: ActivityType, durationMinutes: Float, avgSpeedMs: Float = 0f): Float {
        require(durationMinutes.isFinite() && durationMinutes >= 0f) { "Duration must be non-negative and finite" }
        return calculateCaloriesPerMinute(weightKg, activity, avgSpeedMs) * durationMinutes
    }
}
