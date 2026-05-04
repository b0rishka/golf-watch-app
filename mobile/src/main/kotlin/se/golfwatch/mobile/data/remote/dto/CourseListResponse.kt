package se.golfwatch.mobile.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class CourseListResponse(
    val generated: String,
    val count: Int,
    val courses: List<CourseDto>,
)

@Serializable
data class CourseDto(
    val id: Long,
    val osmType: String,
    val name: String,
    val lat: Double,
    val lon: Double,
    val bbox: BboxDto,
)

@Serializable
data class BboxDto(
    val minLat: Double,
    val minLon: Double,
    val maxLat: Double,
    val maxLon: Double,
)
