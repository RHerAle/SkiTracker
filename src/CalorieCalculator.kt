// SkiTracker
// © 2026 RHerAle
// SPDX-License-Identifier: AGPL-3.0-only
package com.skitracker.service

import com.skitracker.data.ActivityType

/**
 * Calculates calories burned while skiing using MET formula
 * Formula: Calories/min = (MET × weight_kg × 3.5) ÷ 200
 */
class CalorieCalculator {
    
    // MET values for different skiing intensities
    private val MET_LIGHT_SKIING = 4.3f      // Leisurely, beginner
    private val MET_MODERATE_SKIING = 5.3f   // General skiing
    private val MET_VIGOROUS_SKIING = 8.0f   // Racing, aggressive
    private val MET_LIFT = 1.5f              // Sitting on lift (slightly above resting)
    private val MET_STATIONARY = 1.3f        // Standing around
    
    // Speed thresholds for intensity (m/s)
    private val VIGOROUS_SPEED_THRESHOLD = 15f  // 54 km/h
    private val MODERATE_SPEED_THRESHOLD = 8f   // 28.8 km/h
    
    /**
     * Calculate calories burned per minute
     * 
     * @param weightKg User's weight in kilograms
     * @param activity Current activity type
     * @param speedMs Current speed in m/s
     * @return Calories burned per minute
     */
    fun calculateCaloriesPerMinute(
        weightKg: Float,
        activity: ActivityType,
        speedMs: Float = 0f
    ): Float {
        val met = when (activity) {
            ActivityType.SKIING -> {
                // Adjust MET based on skiing speed
                when {
                    speedMs >= VIGOROUS_SPEED_THRESHOLD -> MET_VIGOROUS_SKIING
                    speedMs >= MODERATE_SPEED_THRESHOLD -> MET_MODERATE_SKIING
                    else -> MET_LIGHT_SKIING
                }
            }
            ActivityType.LIFT -> MET_LIFT
            ActivityType.STATIONARY -> MET_STATIONARY
        }
        
        // MET Formula: Calories/min = (MET × weight_kg × 3.5) ÷ 200
        return (met * weightKg * 3.5f) / 200f
    }
    
    /**
     * Calculate total calories for a time period
     * 
     * @param weightKg User's weight in kilograms
     * @param activity Activity type
     * @param durationMinutes Duration in minutes
     * @param avgSpeedMs Average speed in m/s
     * @return Total calories burned
     */
    fun calculateTotalCalories(
        weightKg: Float,
        activity: ActivityType,
        durationMinutes: Float,
        avgSpeedMs: Float = 0f
    ): Float {
        val caloriesPerMinute = calculateCaloriesPerMinute(weightKg, activity, avgSpeedMs)
        return caloriesPerMinute * durationMinutes
    }
}
