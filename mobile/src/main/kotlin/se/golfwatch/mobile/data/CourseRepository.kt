package se.golfwatch.mobile.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import se.golfwatch.mobile.data.local.CourseDao
import se.golfwatch.mobile.data.local.CourseEntity
import se.golfwatch.mobile.data.model.CourseOverview
import se.golfwatch.mobile.data.remote.CourseApi
import se.golfwatch.mobile.data.remote.dto.CourseDto
import javax.inject.Inject

class CourseRepository @Inject constructor(
    private val api: CourseApi,
    private val dao: CourseDao,
) {
    fun observeCourses(): Flow<List<CourseOverview>> =
        dao.observeAll().map { it.map(CourseEntity::toOverview) }

    suspend fun refreshCourses() {
        val response = api.fetchCourseList()
        dao.upsertAll(response.courses.map(CourseDto::toEntity))
    }

    suspend fun hasCachedCourses(): Boolean = dao.count() > 0
}

private fun CourseEntity.toOverview() = CourseOverview(
    id = id,
    osmType = osmType,
    name = name,
    lat = lat,
    lon = lon,
    bboxMinLat = bboxMinLat,
    bboxMinLon = bboxMinLon,
    bboxMaxLat = bboxMaxLat,
    bboxMaxLon = bboxMaxLon,
)

private fun CourseDto.toEntity() = CourseEntity(
    id = id,
    osmType = osmType,
    name = name,
    lat = lat,
    lon = lon,
    bboxMinLat = bbox.minLat,
    bboxMinLon = bbox.minLon,
    bboxMaxLat = bbox.maxLat,
    bboxMaxLon = bbox.maxLon,
)
