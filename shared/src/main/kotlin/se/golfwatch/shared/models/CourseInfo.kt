package se.golfwatch.shared.models

import kotlinx.serialization.Serializable

@Serializable
data class CourseInfo(
    val id: Long,
    val name: String,
    val lat: Double,
    val lon: Double,
)
