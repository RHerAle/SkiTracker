// SkiTracker
// © 2026 RHerAle
// SPDX-License-Identifier: AGPL-3.0-only
package com.skitracker.data

enum class ActivityType {
    SKIING,     // Going downhill
    LIFT,       // On ski lift
    STATIONARY  // Not moving
}

/**
 * Single GPS location point
 */
data class LocationPoint(
    val timestamp: Long,
    val latitude: Double,
    val longitude: Double,
    val altitude: Double,
    val speed: Float,  // m/s
    val accuracy: Float // meters
)
