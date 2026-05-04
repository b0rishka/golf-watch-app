package se.golfwatch.mobile.data.model

data class CourseOverview(
    val id: Long,
    val osmType: String,
    val name: String,
    val lat: Double,
    val lon: Double,
    val bboxMinLat: Double,
    val bboxMinLon: Double,
    val bboxMaxLat: Double,
    val bboxMaxLon: Double,
)
