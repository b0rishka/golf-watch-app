package se.golfwatch.shared.models

import kotlinx.serialization.Serializable

@Serializable
data class HoleData(
    val number: Int,
    val par: Int?,
    val features: List<GolfFeature>,
)
