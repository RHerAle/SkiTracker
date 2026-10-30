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

/**
 * Los puntos en trozos seguidos con el mismo tramo, en orden: entre dos trozos hubo una pausa.
 * Optimized single-pass segmentation: avoids repeated `lastOrNull()`/`last()` list lookups and allocations on every item.
 */
fun List<LocationPoint>.tramos(): List<List<LocationPoint>> {
    if (isEmpty()) return emptyList()
    val trozos = ArrayList<List<LocationPoint>>()
    var currentSegment = this[0].segmento
    var currentChunk = ArrayList<LocationPoint>()
    currentChunk.add(this[0])

    for (i in 1 until size) {
        val p = this[i]
        if (p.segmento == currentSegment) {
            currentChunk.add(p)
        } else {
            trozos.add(currentChunk)
            currentSegment = p.segmento
            currentChunk = ArrayList()
            currentChunk.add(p)
        }
    }
    trozos.add(currentChunk)
    return trozos
}
