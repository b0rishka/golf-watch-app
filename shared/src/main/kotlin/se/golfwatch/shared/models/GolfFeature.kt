package se.golfwatch.shared.models

import kotlinx.serialization.Serializable

@Serializable
data class GolfFeature(
    val type: FeatureType,
    // Polygon ring or polyline; each point is [lon, lat]
    val coordinates: List<List<Double>>,
)

@Serializable
enum class FeatureType {
    FAIRWAY,
    GREEN,
    BUNKER,
    WATER,
    TEE,
    ROUGH,
}
