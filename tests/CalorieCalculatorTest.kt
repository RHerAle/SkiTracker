// SkiTracker
// © 2026 RHerAle
// SPDX-License-Identifier: AGPL-3.0-only
package com.skitracker.service

import com.skitracker.data.ActivityType
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for CalorieCalculator
 */
class CalorieCalculatorTest {
    
    private lateinit var calculator: CalorieCalculator
    
    @Before
    fun setup() {
        calculator = CalorieCalculator()
    }
    
    @Test
    fun testCaloriesPerMinute_ModerateSkiing() {
        val weightKg = 70f
        val speedMs = 10f // 36 km/h - moderate skiing
        
        val caloriesPerMin = calculator.calculateCaloriesPerMinute(
            weightKg,
            ActivityType.SKIING,
            speedMs
        )
        
        // Expected: (5.3 * 70 * 3.5) / 200 = 6.4925
        assertTrue(caloriesPerMin > 6.0f && caloriesPerMin < 7.0f)
    }
    
    @Test
    fun testCaloriesPerMinute_VigorousSkiing() {
        val weightKg = 80f
        val speedMs = 18f // 64.8 km/h - vigorous skiing
        
        val caloriesPerMin = calculator.calculateCaloriesPerMinute(
            weightKg,
            ActivityType.SKIING,
            speedMs
        )
        
        // Expected: (8.0 * 80 * 3.5) / 200 = 11.2
        assertTrue(caloriesPerMin > 10.5f && caloriesPerMin < 12.0f)
    }
    
    @Test
    fun testCaloriesPerMinute_OnLift() {
        val weightKg = 70f
        
        val caloriesPerMin = calculator.calculateCaloriesPerMinute(
            weightKg,
            ActivityType.LIFT,
            0f // Speed doesn't matter for lift
        )
        
        // Expected: (1.5 * 70 * 3.5) / 200 = 1.8375
        assertTrue(caloriesPerMin > 1.5f && caloriesPerMin < 2.0f)
    }
    
    @Test
    fun testTotalCalories() {
        val weightKg = 75f
        val durationMinutes = 30f
        val avgSpeedMs = 12f
        
        val totalCalories = calculator.calculateTotalCalories(
            weightKg,
            ActivityType.SKIING,
            durationMinutes,
            avgSpeedMs
        )
        
        // Should be around 30 * 6.98 = 209.4 calories
        assertTrue(totalCalories > 200f && totalCalories < 220f)
    }

    @Test
    fun testCaloriesScaling_WithCustomWeights() {
        val speedMs = 10f // Moderate skiing, MET = 5.3
        val cal60 = calculator.calculateCaloriesPerMinute(60f, ActivityType.SKIING, speedMs)
        val cal90 = calculator.calculateCaloriesPerMinute(90f, ActivityType.SKIING, speedMs)
        val cal120 = calculator.calculateCaloriesPerMinute(120f, ActivityType.SKIING, speedMs)

        // MET formula is directly proportional to weight: (MET * weight * 3.5) / 200
        assertEquals(cal60 * 1.5f, cal90, 0.01f)
        assertEquals(cal60 * 2.0f, cal120, 0.01f)
    }

    @Test
    fun testCaloriesPerMinute_CustomWeight_LightSkiing() {
        val customWeight = 85f
        val speedMs = 6f // Light skiing (< 8 m/s), MET = 4.3
        val cal = calculator.calculateCaloriesPerMinute(customWeight, ActivityType.SKIING, speedMs)
        val expected = (4.3f * 85f * 3.5f) / 200f
        assertEquals(expected, cal, 0.01f)
    }

    @Test
    fun testCaloriesPerMinute_CustomWeight_Stationary() {
        val customWeight = 95f
        val cal = calculator.calculateCaloriesPerMinute(customWeight, ActivityType.STATIONARY)
        val expected = (1.3f * 95f * 3.5f) / 200f
        assertEquals(expected, cal, 0.01f)
    }

    @Test
    fun testTotalCalories_CustomWeight_Session() {
        val customWeight = 68f
        // 45 minutes of moderate skiing + 15 minutes of lift
        val skiingCal = calculator.calculateTotalCalories(customWeight, ActivityType.SKIING, 45f, 10f)
        val liftCal = calculator.calculateTotalCalories(customWeight, ActivityType.LIFT, 15f, 5f)
        val total = skiingCal + liftCal

        val expectedSkiing = (5.3f * 68f * 3.5f / 200f) * 45f
        val expectedLift = (1.5f * 68f * 3.5f / 200f) * 15f
        assertEquals(expectedSkiing + expectedLift, total, 0.05f)
    }
}
