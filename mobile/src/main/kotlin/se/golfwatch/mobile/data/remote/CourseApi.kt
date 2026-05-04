package se.golfwatch.mobile.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import se.golfwatch.mobile.data.remote.dto.CourseListResponse
import javax.inject.Inject

private const val COURSES_URL = "https://raw.githubusercontent.com/b0rishka/golf-watch-app/main/backend/data/courses.json"

class CourseApi @Inject constructor(private val client: HttpClient) {
    suspend fun fetchCourseList(): CourseListResponse = client.get(COURSES_URL).body()
}
