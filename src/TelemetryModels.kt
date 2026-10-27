// SkiTracker
// © 2026 RHerAle
// SPDX-License-Identifier: AGPL-3.0-only
package com.skitracker.data

// ponytail: compact telemetry types
enum class ActivityType { SKIING, LIFT, STATIONARY }

data class LocationPoint(
    val timestamp: Long,
    val latitude: Double,
    val longitude: Double,
    val altitude: Double,
    val speed: Float,
    val accuracy: Float,
    /** Tramo de la sesión: cada pausa empieza uno. Las sesiones guardadas antes no lo traen y se leen como 0. */
    val segmento: Int = 0
)

/** Los puntos en trozos seguidos con el mismo tramo, en orden: entre dos trozos hubo una pausa. */
fun List<LocationPoint>.tramos(): List<List<LocationPoint>> {
    if (isEmpty()) return emptyList()
    val trozos = ArrayList<MutableList<LocationPoint>>()
    var currentList: MutableList<LocationPoint>? = null
    var currentSegment: Int? = null

    for (p in this) {
        // Bolt performance optimization: Cache the current segment and sublist reference in local variables.
        // This eliminates redundant O(N) lastOrNull() and last() calls on trozos and sublists during telemetry segmentation.
        if (currentList != null && p.segmento == currentSegment) {
            currentList.add(p)
        } else {
            val newList = mutableListOf(p)
            currentList = newList
            currentSegment = p.segmento
            trozos.add(newList)
        }
    }
    return trozos
}
