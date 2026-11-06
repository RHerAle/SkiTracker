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
    // ⚡ Bolt optimization: avoids repeated trozos.lastOrNull() and ultimo.last() lookups per element.
    // Tracking current segment ID and active chunk avoids redundant collection inspections and iterator overhead.
    val n = size
    if (n == 0) return emptyList()

    val trozos = ArrayList<MutableList<LocationPoint>>()
    var currentChunk = ArrayList<LocationPoint>()
    var currentSegment = this[0].segmento
    trozos.add(currentChunk)

    for (i in 0 until n) {
        val p = this[i]
        if (p.segmento == currentSegment) {
            currentChunk.add(p)
        } else {
            currentSegment = p.segmento
            currentChunk = ArrayList()
            currentChunk.add(p)
            trozos.add(currentChunk)
        }
    }
    return trozos
}
