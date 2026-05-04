package se.golfwatch.mobile.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "courses")
data class CourseEntity(
    @PrimaryKey val id: Long,
    val osmType: String,
    val name: String,
    val lat: Double,
    val lon: Double,
    val bboxMinLat: Double,
    val bboxMinLon: Double,
    val bboxMaxLat: Double,
    val bboxMaxLon: Double,
)
