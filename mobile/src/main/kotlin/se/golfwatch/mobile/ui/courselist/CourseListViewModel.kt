package se.golfwatch.mobile.ui.courselist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import se.golfwatch.mobile.data.CourseRepository
import se.golfwatch.mobile.data.model.CourseOverview
import javax.inject.Inject

data class CourseListUiState(
    val courses: List<CourseOverview> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = true,
    val error: String? = null,
) {
    val filteredCourses: List<CourseOverview>
        get() =
            if (searchQuery.isBlank()) {
                courses
            } else {
                courses.filter { it.name.contains(searchQuery, ignoreCase = true) }
            }
}

@HiltViewModel
class CourseListViewModel
    @Inject
    constructor(
        private val repository: CourseRepository,
    ) : ViewModel() {
        private val searchQuery = MutableStateFlow("")
        private val isLoading = MutableStateFlow(true)
        private val error = MutableStateFlow<String?>(null)

        val uiState: StateFlow<CourseListUiState> =
            combine(
                repository.observeCourses(),
                searchQuery,
                isLoading,
                error,
            ) { courses, query, loading, err ->
                CourseListUiState(
                    courses = courses,
                    searchQuery = query,
                    isLoading = loading,
                    error = err,
                )
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = CourseListUiState(),
            )

        init {
            refresh()
        }

        fun onSearchQueryChange(query: String) {
            searchQuery.value = query
        }

        fun refresh() {
            viewModelScope.launch {
                isLoading.value = true
                error.value = null
                try {
                    repository.refreshCourses()
                } catch (e: Exception) {
                    error.value = "Couldn't load courses. Check your connection."
                } finally {
                    isLoading.value = false
                }
            }
        }
    }
