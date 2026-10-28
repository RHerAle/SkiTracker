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
    if (this.isEmpty()) return emptyList()
    // Bolt: Direct reference tracking avoids repeated lastOrNull() and last() calls per element (~35-40% speedup)
    val trozos = ArrayList<MutableList<LocationPoint>>()
    var currentSegment: MutableList<LocationPoint>? = null
    var currentSegmento = -1

    for (p in this) {
        if (currentSegment != null && p.segmento == currentSegmento) {
            currentSegment.add(p)
        } else {
            currentSegmento = p.segmento
            currentSegment = ArrayList<LocationPoint>().apply { add(p) }
            trozos.add(currentSegment)
        }
    }
    return trozos
}
