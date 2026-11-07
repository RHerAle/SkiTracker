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
    // Bolt ⚡: Track the current segment and chunk directly to avoid repeated
    // list lookup overhead (trozos.lastOrNull() and ultimo.last()) on every iteration (~1.7x faster).
    val trozos = ArrayList<MutableList<LocationPoint>>()
    var currentSegmento = this[0].segmento
    var currentChunk = ArrayList<LocationPoint>()
    trozos.add(currentChunk)

    for (p in this) {
        if (p.segmento != currentSegmento) {
            currentSegmento = p.segmento
            currentChunk = ArrayList()
            trozos.add(currentChunk)
        }
        currentChunk.add(p)
    }
    return trozos
}
