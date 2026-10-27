// SkiTracker
// © 2026 RHerAle
// SPDX-License-Identifier: AGPL-3.0-only
package com.skitracker.service

import com.skitracker.data.ActivityType
import org.junit.Assert.*
import org.junit.Test

class CalorieCalculatorTest {
    private val calc = CalorieCalculator()

    @Test
    fun testCaloriesPerMinute_ModerateSkiing() {
        val c = calc.calculateCaloriesPerMinute(70f, ActivityType.SKIING, 10f)
        assertTrue(c in 6.0f..7.0f)
    }

    @Test
    fun testCaloriesPerMinute_VigorousSkiing() {
        val c = calc.calculateCaloriesPerMinute(80f, ActivityType.SKIING, 18f)
        assertTrue(c in 10.5f..12.0f)
    }

    @Test
    fun testCaloriesPerMinute_OnLift() {
        val c = calc.calculateCaloriesPerMinute(70f, ActivityType.LIFT, 0f)
        assertTrue(c in 1.5f..2.0f)
    }

    @Test
    fun testTotalCalories() {
        val c = calc.calculateTotalCalories(75f, ActivityType.SKIING, 30f, 12f)
        assertTrue(c in 200f..220f)
    }

    @Test
    fun testCaloriesScaling_WithCustomWeights() {
        val c60 = calc.calculateCaloriesPerMinute(60f, ActivityType.SKIING, 10f)
        assertEquals(c60 * 1.5f, calc.calculateCaloriesPerMinute(90f, ActivityType.SKIING, 10f), 0.01f)
        assertEquals(c60 * 2.0f, calc.calculateCaloriesPerMinute(120f, ActivityType.SKIING, 10f), 0.01f)
    }

    @Test
    fun testCaloriesPerMinute_CustomWeight_LightSkiing() {
        assertEquals((4.3f * 85f * 3.5f) / 200f, calc.calculateCaloriesPerMinute(85f, ActivityType.SKIING, 6f), 0.01f)
    }

    @Test
    fun testCaloriesPerMinute_CustomWeight_Stationary() {
        assertEquals((1.3f * 95f * 3.5f) / 200f, calc.calculateCaloriesPerMinute(95f, ActivityType.STATIONARY), 0.01f)
    }

    @Test
    fun testTotalCalories_CustomWeight_Session() {
        val total = calc.calculateTotalCalories(68f, ActivityType.SKIING, 45f, 10f) +
            calc.calculateTotalCalories(68f, ActivityType.LIFT, 15f, 5f)
        val expected = ((5.3f * 68f * 3.5f / 200f) * 45f) + ((1.5f * 68f * 3.5f / 200f) * 15f)
        assertEquals(expected, total, 0.05f)
    }

    @Test
    fun testDefensiveSanitization_InvalidInputs() {
        assertEquals(0f, calc.calculateCaloriesPerMinute(-70f, ActivityType.SKIING, 10f), 0.001f)
        assertEquals(0f, calc.calculateCaloriesPerMinute(0f, ActivityType.SKIING, 10f), 0.001f)
        assertEquals(0f, calc.calculateCaloriesPerMinute(Float.NaN, ActivityType.SKIING, 10f), 0.001f)
        assertEquals(0f, calc.calculateCaloriesPerMinute(Float.POSITIVE_INFINITY, ActivityType.SKIING, 10f), 0.001f)
        assertEquals(0f, calc.calculateCaloriesPerMinute(70f, ActivityType.SKIING, -5f), 0.001f)
        assertEquals(0f, calc.calculateCaloriesPerMinute(70f, ActivityType.SKIING, Float.NaN), 0.001f)
        assertEquals(0f, calc.calculateTotalCalories(70f, ActivityType.SKIING, -10f, 10f), 0.001f)
        assertEquals(0f, calc.calculateTotalCalories(70f, ActivityType.SKIING, Float.NaN, 10f), 0.001f)
    }
}
